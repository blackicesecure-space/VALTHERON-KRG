package com.example.data.embedding

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiEmbeddingModel(
    override val modelId: String = "text-embedding-004",
    override val modelVersion: String = "v1",
    override val dimensions: Int = 768,
    override val distanceMetric: DistanceMetric = DistanceMetric.COSINE,
    override val normalization: String? = "L2"
) : EmbeddingModel {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun embed(text: String): FloatArray = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            throw IllegalStateException("Gemini API Key is missing. Please configure it in AI Studio Secrets.")
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelId:embedContent?key=$apiKey"
        
        val jsonBody = JSONObject().apply {
            put("model", "models/$modelId")
            put("content", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", text)))
            })
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: "Unknown error"
                throw RuntimeException("Gemini Embedding API failed (${response.code}): $errorBody")
            }

            val responseBody = response.body?.string() ?: throw RuntimeException("Empty response from Gemini Embedding API")
            val jsonResponse = JSONObject(responseBody)
            
            val embeddingObj = jsonResponse.optJSONObject("embedding")
                ?: throw RuntimeException("Invalid embedding response structure: $responseBody")
            val valuesArray = embeddingObj.optJSONArray("values")
                ?: throw RuntimeException("Missing 'values' array in embedding response: $responseBody")

            val result = FloatArray(valuesArray.length())
            for (i in 0 until valuesArray.length()) {
                result[i] = valuesArray.getDouble(i).toFloat()
            }

            if (result.size != dimensions) {
                // If dimension differs from expected, accept real dimensions from provider
                // but validate non-empty
            }
            if (result.isEmpty()) {
                throw RuntimeException("Generated embedding is empty.")
            }

            result
        }
    }
}
