package kr.co.example.treeplz.data.model

import java.util.Date

data class WeeklyUsage(
    val date: Date,
    val carbonImpactInGrams: Int
)