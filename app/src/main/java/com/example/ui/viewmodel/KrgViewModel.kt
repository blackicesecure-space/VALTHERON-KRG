package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.KrgRepository
import com.example.data.room.AppDatabase
import com.example.data.room.KrgAuditLogEntity
import com.example.data.room.KrgCalculationEntity
import com.example.data.room.KrgEmbeddingEntity
import com.example.data.room.KrgWorkspaceItemEntity
import com.example.data.service.GematriaResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class ScalingMetrics(
    val activeConnections: Int = 24,
    val maxConnections: Int = 100,
    val queueDepth: Int = 2,
    val hpaReplicas: Int = 3,
    val cacheHitRate: Float = 94.8f,
    val vectorIndexType: String = "pgvector IVFFlat (lists=100)"
)

class KrgViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: KrgRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = KrgRepository(database.krgDao())
    }

    val workspaceItems: StateFlow<List<KrgWorkspaceItemEntity>> = repository.allWorkspaceItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val embeddings: StateFlow<List<KrgEmbeddingEntity>> = repository.allEmbeddings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<KrgAuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calculations: StateFlow<List<KrgCalculationEntity>> = repository.allCalculations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var searchQuery by mutableStateOf("")
    var aiResonanceResult by mutableStateOf("")
    var isSearching by mutableStateOf(false)
    var isIngesting by mutableStateOf(false)
    var scalingMetrics by mutableStateOf(ScalingMetrics())
    var exportedDataContent by mutableStateOf<String?>(null)
    var exportedFileName by mutableStateOf<String?>(null)

    fun exportData(format: String) {
        val list = calculations.value
        if (format == "CSV") {
            exportedDataContent = com.example.data.service.ExportService.exportToCsv(list)
            exportedFileName = "valtheron_calculations_${System.currentTimeMillis()}.csv"
        } else {
            exportedDataContent = com.example.data.service.ExportService.exportToJson(list)
            exportedFileName = "valtheron_calculations_${System.currentTimeMillis()}.json"
        }
    }

    fun dismissExport() {
        exportedDataContent = null
        exportedFileName = null
    }

    fun addWorkspaceItem(title: String, content: String, sourceType: String) {
        viewModelScope.launch {
            isIngesting = true
            repository.addWorkspaceItem(title, content, sourceType)
            isIngesting = false
        }
    }

    fun deleteWorkspaceItem(id: String) {
        viewModelScope.launch {
            repository.deleteWorkspaceItem(id)
        }
    }

    fun saveCalculation(result: GematriaResult) {
        viewModelScope.launch {
            repository.saveCalculation(result)
        }
    }

    fun deleteCalculation(id: String) {
        viewModelScope.launch {
            repository.deleteCalculation(id)
        }
    }

    fun performSemanticSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            isSearching = true
            aiResonanceResult = repository.performSemanticSearch(query)
            isSearching = false
        }
    }
}
