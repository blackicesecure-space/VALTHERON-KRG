package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.GematriaCalculatorCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.KrgViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResonantSearchScreen(viewModel: KrgViewModel) {
    val embeddings by viewModel.embeddings.collectAsState()
    val savedCalculations by viewModel.calculations.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resonant Semantic Gematria Search") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Interactive Gematria Calculator Component with Room Save
                GematriaCalculatorCard(
                    onSaveCalculation = { result ->
                        viewModel.saveCalculation(result)
                    }
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Vector Similarity Engine (pgvector IVFFlat)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Enter semantic query or concept...") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.performSemanticSearch(searchQuery) }) {
                                    Icon(Icons.Default.Search, contentDescription = "Search", tint = ValtheronTealNeon)
                                }
                            },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.performSemanticSearch(searchQuery) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = searchQuery.isNotBlank() && !viewModel.isSearching
                        ) {
                            if (viewModel.isSearching) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.background)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Computing Vector Distance...")
                            } else {
                                Icon(Icons.Default.Star, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Execute Resonant Search")
                            }
                        }
                    }
                }
            }

            if (viewModel.aiResonanceResult.isNotBlank()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = ValtheronTealNeon,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gemini AI Semantic Resonance Synthesis",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.aiResonanceResult,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ValtheronTextPrimary
                            )
                        }
                    }
                }
            }

            item {
                // Gematria Value Distribution Chart Component
                com.example.ui.components.GematriaDistributionChart(calculations = savedCalculations)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Gematria Calculation Sessions (${savedCalculations.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.exportData("CSV") },
                            enabled = savedCalculations.isNotEmpty(),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CSV", style = MaterialTheme.typography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = { viewModel.exportData("JSON") },
                            enabled = savedCalculations.isNotEmpty(),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("JSON", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            if (viewModel.exportedDataContent != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = ValtheronTealNeon)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Exported File: ${viewModel.exportedFileName}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(onClick = { viewModel.dismissExport() }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = ValtheronTextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.background,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                Box(modifier = Modifier.padding(12.dp)) {
                                    LazyColumn {
                                        item {
                                            Text(
                                                text = viewModel.exportedDataContent ?: "",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ValtheronTextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.dismissExport() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Done / Dismiss Preview")
                            }
                        }
                    }
                }
            }

            if (savedCalculations.isEmpty()) {
                item {
                    Text(
                        text = "No previous calculation sessions saved in Room database yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ValtheronTextSecondary
                    )
                }
            } else {
                savedCalculations.forEach { calc ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "\"${calc.input}\"",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ValtheronTealNeon
                                    )
                                    IconButton(onClick = { viewModel.deleteCalculation(calc.id) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = ValtheronTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Ordinal: ${calc.ordinalValue} | Reduced: ${calc.reducedValue}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Harmonic: ${calc.resonanceHarmonic}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ValtheronBlueGlow
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Saved: ${dateFormat.format(Date(calc.createdAt))}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ValtheronTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Indexed Vector Embeddings Store (${embeddings.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (embeddings.isEmpty()) {
                item {
                    Text(
                        text = "No embeddings indexed. Ingest items in the Workspace tab.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ValtheronTextSecondary
                    )
                }
            } else {
                embeddings.forEach { embedding ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Source: ${embedding.sourceType}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Vector: 1536d L2",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ValtheronTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Embedding Vector: ${embedding.embeddingVectorSummary}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ValtheronTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Hash: ${embedding.contentHash.take(16)}...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ValtheronTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
