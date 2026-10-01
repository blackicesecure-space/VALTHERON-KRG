package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.example.ui.viewmodel.KrgViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScalingMonitorScreen(viewModel: KrgViewModel) {
    val metrics = viewModel.scalingMetrics

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Heavy Load & Scaling Monitor") },
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
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = ValtheronTealNeon)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PostgreSQL Connection Pool & PgBouncer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { metrics.activeConnections.toFloat() / metrics.maxConnections.toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            color = ValtheronTealNeon,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Active Connections: ${metrics.activeConnections}", style = MaterialTheme.typography.bodySmall, color = ValtheronTextSecondary)
                            Text("Max Pool: ${metrics.maxConnections}", style = MaterialTheme.typography.bodySmall, color = ValtheronTextSecondary)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = ValtheronBlueGlow)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Queue Depth", style = MaterialTheme.typography.bodySmall, color = ValtheronTextSecondary)
                            Text("${metrics.queueDepth} msgs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ValtheronTextPrimary)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = ValtheronAccentPurple)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("HPA Replicas", style = MaterialTheme.typography.bodySmall, color = ValtheronTextSecondary)
                            Text("${metrics.hpaReplicas} pods", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ValtheronTextPrimary)
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = ValtheronSuccess)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Vector Indexing & Cache Performance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Vector Index: ${metrics.vectorIndexType}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ValtheronTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Redis Cache Hit Rate: ${metrics.cacheHitRate}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ValtheronSuccess
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Encryption: AES-256-GCM In-Memory Decryption Active",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ValtheronTextSecondary
                        )
                    }
                }
            }
        }
    }
}
