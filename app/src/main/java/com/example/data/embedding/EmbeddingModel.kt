package com.example.data.embedding

enum class DistanceMetric {
    COSINE,
    L2,
    DOT_PRODUCT
}

interface EmbeddingModel {
    val modelId: String
    val modelVersion: String
    val dimensions: Int
    val distanceMetric: DistanceMetric
    val normalization: String?

    suspend fun embed(text: String): FloatArray
}
