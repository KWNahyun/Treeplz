package kr.co.example.treeplz.data.model

data class UsageStats(
    val requests: Int,
    val timeInMinutes: Int,
    val tokens: Double,
    val carbonImpactInGrams: Int
)