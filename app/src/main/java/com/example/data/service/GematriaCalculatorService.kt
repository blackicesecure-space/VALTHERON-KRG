package com.example.data.service

data class GematriaResult(
    val input: String,
    val ordinalValue: Int,
    val reducedValue: Int,
    val characterCount: Int,
    val resonanceHarmonic: Double
)

object GematriaCalculatorService {
    fun calculate(text: String): GematriaResult {
        val cleanText = text.uppercase().filter { it in 'A'..'Z' || it in '0'..'9' }
        var ordinalSum = 0
        for (char in cleanText) {
            ordinalSum += when (char) {
                in 'A'..'Z' -> char - 'A' + 1
                in '0'..'9' -> char.digitToInt()
                else -> 0
            }
        }
        
        // Reduced Gematria (digital root summation)
        var reduced = ordinalSum
        while (reduced > 9 && reduced != 11 && reduced != 22 && reduced != 33) {
            reduced = reduced.toString().map { it.digitToInt() }.sum()
        }

        val harmonic = if (cleanText.isNotEmpty()) {
            (ordinalSum.toDouble() * 1.618) / (cleanText.length.toDouble() + 1.0)
        } else {
            0.0
        }

        return GematriaResult(
            input = text,
            ordinalValue = ordinalSum,
            reducedValue = reduced,
            characterCount = cleanText.length,
            resonanceHarmonic = String.format("%.2f", harmonic).toDouble()
        )
    }
}
