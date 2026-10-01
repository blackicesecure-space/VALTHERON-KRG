package com.example.data.room

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class KrgTypeConverters {
    private val moshi = Moshi.Builder().build()
    private val mapType = Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    private val mapAdapter = moshi.adapter<Map<String, Any>>(mapType)

    @TypeConverter
    fun fromStringMap(map: Map<String, Any>?): String {
        return mapAdapter.toJson(map ?: emptyMap())
    }

    @TypeConverter
    fun toStringMap(json: String?): Map<String, Any> {
        if (json.isNullOrBlank()) return emptyMap()
        return try {
            mapAdapter.fromJson(json) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    @TypeConverter
    fun fromFloatArray(array: FloatArray?): String {
        val list = (array ?: floatArrayOf()).toList()
        val listType = Types.newParameterizedType(List::class.java, Float::class.javaObjectType)
        val listAdapter = moshi.adapter<List<Float>>(listType)
        return listAdapter.toJson(list)
    }

    @TypeConverter
    fun toFloatArray(json: String?): FloatArray {
        if (json.isNullOrBlank()) return floatArrayOf()
        return try {
            val listType = Types.newParameterizedType(List::class.java, Float::class.javaObjectType)
            val listAdapter = moshi.adapter<List<Float>>(listType)
            val list = listAdapter.fromJson(json) ?: emptyList()
            list.toFloatArray()
        } catch (e: Exception) {
            floatArrayOf()
        }
    }
}
