package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.room.KrgCalculationEntity
import com.example.ui.theme.*

@Composable
fun GematriaDistributionChart(
    calculations: List<KrgCalculationEntity>,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("gematria_distribution_chart"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Gematria Value Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ordinal Sum Frequency Analysis",
                            style = MaterialTheme.typography.bodySmall,
                            color = ValtheronTextSecondary
                        )
                    }
                }

                Surface(
                    color = ValtheronTealNeon.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${calculations.size} Records",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = ValtheronTealNeon,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (calculations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No saved calculation records available for distribution chart.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ValtheronTextSecondary
                    )
                }
            } else {
                val primaryColor = MaterialTheme.colorScheme.primary
                val tealColor = ValtheronTealNeon

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val barWidth = (canvasWidth / calculations.size.coerceAtLeast(1)).coerceIn(8f, 40f)
                        val maxVal = calculations.maxOfOrNull { it.ordinalValue }?.toFloat() ?: 1f

                        calculations.forEachIndexed { index, calc ->
                            val spacing = 12f
                            val x = index * (barWidth + spacing) + spacing
                            val barHeight = (calc.ordinalValue / maxVal) * (canvasHeight - 30f)
                            val y = canvasHeight - barHeight - 20f

                            drawRect(
                                color = if (index % 2 == 0) primaryColor else tealColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Min Ordinal: ${calculations.minOfOrNull { it.ordinalValue } ?: 0}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ValtheronTextSecondary
                    )
                    Text(
                        text = "Max Ordinal: ${calculations.maxOfOrNull { it.ordinalValue } ?: 0}",
                        style = MaterialTheme.typography.labelSmall,
                        color = ValtheronTextSecondary
                    )
                }
            }
        }
    }
}
