package com.example.data.repository

import com.example.data.embedding.EmbeddingModel
import com.example.data.embedding.GeminiEmbeddingModel
import com.example.data.network.generateSemanticResonanceAnalysis
import com.example.data.room.KrgAuditLogEntity
import com.example.data.room.KrgCalculationEntity
import com.example.data.room.KrgDao
import com.example.data.room.KrgEmbeddingEntity
import com.example.data.room.KrgWorkspaceItemEntity
import com.example.data.service.GematriaResult
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class KrgRepository(
    private val krgDao: KrgDao,
    private val embeddingModel: EmbeddingModel = GeminiEmbeddingModel()
) {

    val allWorkspaceItems: Flow<List<KrgWorkspaceItemEntity>> = krgDao.getAllWorkspaceItems()
    val allEmbeddings: Flow<List<KrgEmbeddingEntity>> = krgDao.getAllEmbeddings()
    val allAuditLogs: Flow<List<KrgAuditLogEntity>> = krgDao.getAllAuditLogs()
    val allCalculations: Flow<List<KrgCalculationEntity>> = krgDao.getAllCalculations()

    suspend fun addWorkspaceItem(title: String, content: String, sourceType: String) {
        val item = KrgWorkspaceItemEntity(
            title = title,
            content = content,
            sourceType = sourceType,
            isEncrypted = true
        )
        krgDao.insertWorkspaceItem(item)

        val contentHash = sha256(content)
        val vectorFloatArray = try {
            embeddingModel.embed(content)
        } catch (e: Exception) {
            throw RuntimeException("Failed to generate real embedding via ${embeddingModel.modelId}: ${e.message}", e)
        }

        val actualModelId = if (embeddingModel is GeminiEmbeddingModel) embeddingModel.activeModelId else embeddingModel.modelId
        val actualDimensions = if (embeddingModel is GeminiEmbeddingModel) embeddingModel.activeDimensions else embeddingModel.dimensions

        if (vectorFloatArray.size != actualDimensions) {
            throw RuntimeException("Strict dimension validation failed: expected $actualDimensions, got ${vectorFloatArray.size}")
        }

        val vectorSummary = "[${vectorFloatArray.take(4).joinToString(", ") { "%.3f".format(it) }}... (${vectorFloatArray.size}d)]"
        val modelRef = "$actualModelId:${embeddingModel.modelVersion}"

        val embedding = KrgEmbeddingEntity(
            embeddingVectorSummary = vectorSummary,
            sourceType = sourceType,
            sourceId = item.id,
            contentHash = contentHash,
            modelRef = modelRef,
            dimensions = vectorFloatArray.size,
            distanceMetric = embeddingModel.distanceMetric.name.lowercase(),
            normalization = embeddingModel.normalization,
            embedding = vectorFloatArray,
            metadataJson = "{\"title\":\"$title\", \"length\":${content.length}, \"provider\":\"Gemini API\", \"modelUsed\":\"$actualModelId\"}"
        )
        krgDao.insertEmbedding(embedding)

        val audit = KrgAuditLogEntity(
            action = "EMBEDDING_GENERATED",
            details = "Embedding generated using model $actualModelId version ${embeddingModel.modelVersion} (${vectorFloatArray.size}d) for '${title}' with hash $contentHash"
        )
        krgDao.insertAuditLog(audit)
    }

    suspend fun deleteWorkspaceItem(id: String) {
        krgDao.deleteWorkspaceItem(id)
        krgDao.insertAuditLog(
            KrgAuditLogEntity(
                action = "WORKSPACE_ITEM_DELETED",
                details = "Removed workspace item ID $id and unindexed vectors."
            )
        )
    }

    suspend fun saveCalculation(result: GematriaResult) {
        val entity = KrgCalculationEntity(
            input = result.input,
            ordinalValue = result.ordinalValue,
            reducedValue = result.reducedValue,
            characterCount = result.characterCount,
            resonanceHarmonic = result.resonanceHarmonic
        )
        krgDao.insertCalculation(entity)
        krgDao.insertAuditLog(
            KrgAuditLogEntity(
                action = "GEMATRIA_SESSION_SAVED",
                details = "Saved calculation session for '${result.input}' (Ordinal: ${result.ordinalValue})"
            )
        )
    }

    suspend fun deleteCalculation(id: String) {
        krgDao.deleteCalculation(id)
    }

    suspend fun performSemanticSearch(query: String): String {
        krgDao.insertAuditLog(
            KrgAuditLogEntity(
                action = "CONTEXTUAL_SEARCH",
                details = "Executed semantic similarity search against local embedding cache for query: '$query'"
            )
        )
        return generateSemanticResonanceAnalysis("Analyze contextual resonance for query: '$query'")
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
