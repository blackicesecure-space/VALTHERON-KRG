package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KrgDao {
    @Query("SELECT * FROM krg_workspace_items ORDER BY createdAt DESC")
    fun getAllWorkspaceItems(): Flow<List<KrgWorkspaceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaceItem(item: KrgWorkspaceItemEntity)

    @Query("DELETE FROM krg_workspace_items WHERE id = :id")
    suspend fun deleteWorkspaceItem(id: String)

    @Query("SELECT * FROM krg_embeddings ORDER BY createdAt DESC")
    fun getAllEmbeddings(): Flow<List<KrgEmbeddingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmbedding(embedding: KrgEmbeddingEntity)

    @Query("SELECT * FROM krg_audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<KrgAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: KrgAuditLogEntity)

    @Query("SELECT * FROM krg_calculations ORDER BY createdAt DESC")
    fun getAllCalculations(): Flow<List<KrgCalculationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: KrgCalculationEntity)

    @Query("DELETE FROM krg_calculations WHERE id = :id")
    suspend fun deleteCalculation(id: String)

    @Query("SELECT * FROM krg_embedding_models ORDER BY createdAt DESC")
    fun getAllEmbeddingModels(): Flow<List<KrgEmbeddingModelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmbeddingModel(model: KrgEmbeddingModelEntity)

    @Query("SELECT * FROM krg_symbolic_transforms ORDER BY createdAt DESC")
    fun getAllSymbolicTransforms(): Flow<List<KrgSymbolicTransformEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymbolicTransform(transform: KrgSymbolicTransformEntity)
}

@Dao
interface KrgEmbeddingModelDao {
    @Query("SELECT * FROM krg_embedding_models ORDER BY createdAt DESC")
    fun getAllModels(): Flow<List<KrgEmbeddingModelEntity>>

    @Query("SELECT * FROM krg_embedding_models WHERE modelId = :modelId AND modelVersion = :modelVersion LIMIT 1")
    suspend fun getModelByRef(modelId: String, modelVersion: String): KrgEmbeddingModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: KrgEmbeddingModelEntity)

    @Query("SELECT COUNT(*) FROM krg_embeddings WHERE workspaceId = :workspaceId AND sourceType = :sourceType AND sourceId = :sourceId AND contentHash = :contentHash AND modelRef = :modelRef")
    suspend fun verifyEmbeddingIdentity(
        workspaceId: String,
        sourceType: String,
        sourceId: String,
        contentHash: String,
        modelRef: String
    ): Int
}

@Dao
interface KrgSymbolicTransformDao {
    @Query("SELECT * FROM krg_symbolic_transforms ORDER BY createdAt DESC")
    fun getAllTransforms(): Flow<List<KrgSymbolicTransformEntity>>

    @Query("SELECT * FROM krg_symbolic_transforms WHERE transformId = :transformId AND version = :version LIMIT 1")
    suspend fun getTransformByRef(transformId: String, version: String): KrgSymbolicTransformEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransform(transform: KrgSymbolicTransformEntity)
}
