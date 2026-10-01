package com.example.data.service

import com.example.data.room.KrgCalculationEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportService {
    fun exportToCsv(calculations: List<KrgCalculationEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,Input,OrdinalValue,ReducedValue,CharacterCount,ResonanceHarmonic,CreatedAt\n")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        for (calc in calculations) {
            val dateStr = dateFormat.format(Date(calc.createdAt))
            // Escape quotes in input
            val escapedInput = "\"${calc.input.replace("\"", "\"\"")}\""
            sb.append("${calc.id},${escapedInput},${calc.ordinalValue},${calc.reducedValue},${calc.characterCount},${calc.resonanceHarmonic},\"$dateStr\"\n")
        }
        return sb.toString()
    }

    fun exportToJson(calculations: List<KrgCalculationEntity>): String {
        val sb = StringBuilder()
        sb.append("[\n")
        calculations.forEachIndexed { index, calc ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(calc.createdAt))
            sb.append("  {\n")
            sb.append("    \"id\": \"${calc.id}\",\n")
            sb.append("    \"input\": \"${calc.input.replace("\"", "\\\"")}\",\n")
            sb.append("    \"ordinalValue\": ${calc.ordinalValue},\n")
            sb.append("    \"reducedValue\": ${calc.reducedValue},\n")
            sb.append("    \"characterCount\": ${calc.characterCount},\n")
            sb.append("    \"resonanceHarmonic\": ${calc.resonanceHarmonic},\n")
            sb.append("    \"createdAt\": \"$dateStr\"\n")
            sb.append("  }")
            if (index < calculations.size - 1) sb.append(",")
            sb.append("\n")
        }
        sb.append("]")
        return sb.toString()
    }
}
