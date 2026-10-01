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
    override val modelId: String = "gemini-embedding-2",
    override val modelVersion: String = "v1",
    override val dimensions: Int = 768,
    override val distanceMetric: DistanceMetric = DistanceMetric.COSINE,
    override val normalization: String? = "L2"
) : EmbeddingModel {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Primary: gemini-embedding-2, Fallback: gemini-embedding-001
    private val candidateModels = listOf(
        Pair("gemini-embedding-2", 768),
        Pair("gemini-embedding-001", 768)
    )

    var activeModelId: String = modelId
        private set
    var activeDimensions: Int = dimensions
        private set

    override suspend fun embed(text: String): FloatArray = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            throw IllegalStateException("Gemini API Key is missing. Please configure it in AI Studio Secrets.")
        }

        var lastException: Exception? = null

        for ((mId, expectedDims) in candidateModels) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$mId:embedContent?key=$apiKey"
                
                val jsonBody = JSONObject().apply {
                    put("model", "models/$mId")
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
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: throw RuntimeException("Empty response")
                        val jsonResponse = JSONObject(responseBody)
                        val embeddingObj = jsonResponse.optJSONObject("embedding")
                            ?: throw RuntimeException("Invalid embedding response structure")
                        val valuesArray = embeddingObj.optJSONArray("values")
                            ?: throw RuntimeException("Missing 'values' array")

                        val result = FloatArray(valuesArray.length())
                        for (i in 0 until valuesArray.length()) {
                            result[i] = valuesArray.getDouble(i).toFloat()
                        }

                        // Fail hard if returned vector size != expected dimensions
                        if (result.size != expectedDims) {
                            throw RuntimeException("Dimension mismatch for model $mId: expected $expectedDims, got ${result.size}")
                        }

                        activeModelId = mId
                        activeDimensions = expectedDims
                        return@withContext result
                    } else {
                        val errorBody = response.body?.string() ?: "Unknown error"
                        lastException = RuntimeException("Model $mId failed (${response.code}): $errorBody")
                    }
                }
            } catch (e: Exception) {
                lastException = e
            }
        }
        
        throw RuntimeException("All dedicated embedding models (gemini-embedding-2, gemini-embedding-001) failed. Last error: ${lastException?.message}", lastException)
    }
}
