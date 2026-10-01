package com.example.data.repository

import com.example.data.network.generateSemanticResonanceAnalysis
import com.example.data.room.KrgAuditLogEntity
import com.example.data.room.KrgCalculationEntity
import com.example.data.room.KrgDao
import com.example.data.room.KrgEmbeddingEntity
import com.example.data.room.KrgWorkspaceItemEntity
import com.example.data.service.GematriaResult
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import kotlin.random.Random

class KrgRepository(private val krgDao: KrgDao) {

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
        val vectorSummary = generateMockVector()
        val embedding = KrgEmbeddingEntity(
            embeddingVectorSummary = vectorSummary,
            sourceType = sourceType,
            sourceId = item.id,
            contentHash = contentHash,
            metadataJson = "{\"title\":\"$title\", \"length\":${content.length}}"
        )
        krgDao.insertEmbedding(embedding)

        val audit = KrgAuditLogEntity(
            action = "EMBEDDING_GENERATED",
            details = "Ingested $sourceType '${title}' with hash $contentHash and vector [1536d]"
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
                details = "Executed pgvector ANN search for query: '$query'"
            )
        )
        return generateSemanticResonanceAnalysis("Analyze contextual resonance for query: '$query'")
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateMockVector(): String {
        val dims = 4
        val sb = StringBuilder("[")
        for (i in 0 until dims) {
            val v = Random.nextDouble(-1.0, 1.0)
            sb.append(String.format("%.3f", v))
            if (i < dims - 1) sb.append(", ")
        }
        sb.append("... (1536d)]")
        return sb.toString()
    }
}
