package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.service.GematriaCalculatorService
import com.example.data.service.GematriaResult
import com.example.ui.theme.*

@Composable
fun GematriaCalculatorCard(
    onSaveCalculation: (GematriaResult) -> Unit,
    modifier: Modifier = Modifier
) {
    var queryText by remember { mutableStateOf("") }
    val result: GematriaResult by remember(queryText) {
        mutableStateOf(GematriaCalculatorService.calculate(queryText))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .testTag("gematria_calculator_card"),
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
                            text = "Standard Gematria Calculator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Semantic Ordinal & Harmonic Cipher",
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
                        text = "CIPHER: A=1..Z=26",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = ValtheronTealNeon,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                label = { Text("Enter text to calculate Gematria value...") },
                placeholder = { Text("e.g. Valtheron Resonance") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gematria_calc_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                leadingIcon = {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = ValtheronTextSecondary)
                }
            )

            // Results Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricBox(
                    title = "Ordinal",
                    value = result.ordinalValue.toString(),
                    subtitle = "Sum (A=1)",
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    title = "Reduced",
                    value = result.reducedValue.toString(),
                    subtitle = "Digital Root",
                    modifier = Modifier.weight(1f)
                )
                MetricBox(
                    title = "Harmonic",
                    value = result.resonanceHarmonic.toString(),
                    subtitle = "Phi Ratio",
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    if (queryText.isNotBlank()) {
                        onSaveCalculation(result)
                        queryText = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_calculation_button"),
                enabled = queryText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Session to Room Database", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MetricBox(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = ValtheronTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ValtheronTealNeon
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = ValtheronTextSecondary
            )
        }
    }
}
