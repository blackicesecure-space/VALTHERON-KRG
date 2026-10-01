package com.example.data.room

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "krg_workspace_items")
data class KrgWorkspaceItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val sourceType: String, // AGENT_RUN, USER_MESSAGE, DOCUMENT_CHUNK, AUDIT_LOG
    val isEncrypted: Boolean = true,
    val workspaceId: String = "ws-valtheron-alpha",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "krg_embeddings",
    indices = [
        androidx.room.Index(
            value = ["workspaceId", "sourceType", "sourceId", "contentHash", "modelRef"],
            unique = true
        )
    ]
)
data class KrgEmbeddingEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val embeddingVectorSummary: String,
    val sourceType: String,
    val sourceId: String,
    val contentHash: String,
    val modelRef: String = "text-embedding-004:v1",
    val dimensions: Int = 768,
    val distanceMetric: String = "cosine",
    val normalization: String? = "L2",
    val embedding: FloatArray,
    val workspaceId: String = "ws-valtheron-alpha",
    val userId: String = "valtheron-agent-01",
    val metadataJson: String = "{}",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "krg_audit_logs")
data class KrgAuditLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val workspaceId: String = "ws-valtheron-alpha"
)

@Entity(tableName = "krg_calculations")
data class KrgCalculationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val input: String,
    val ordinalValue: Int,
    val reducedValue: Int,
    val characterCount: Int,
    val resonanceHarmonic: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "krg_embedding_models",
    indices = [androidx.room.Index(value = ["modelId", "modelVersion"], unique = true)]
)
data class KrgEmbeddingModelEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val modelId: String,
    val modelVersion: String,
    val provider: String?,
    val dimensions: Int,
    val distanceMetric: String, // cosine, dot_product, l2
    val normalization: String?,
    val configurationJson: String = "{}",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "krg_symbolic_transforms",
    indices = [androidx.room.Index(value = ["transformId", "version"], unique = true)]
)
data class KrgSymbolicTransformEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val transformId: String,
    val version: String,
    val name: String,
    val description: String?,
    val mappingJson: String, // JSON mapping representation
    val algorithmJson: String?,
    val createdAt: Long = System.currentTimeMillis()
)
