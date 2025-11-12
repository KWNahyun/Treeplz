package kr.co.example.treeplz.ui.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class UsageDetailsUiState(
    val aiRequests: String = "13",
    val timeSpent: String = "26minutes",
    val tokensUsed: String = "7.4k",
    val carbonFootprint: String = "15grams CO2",
    val weeklyTrend: List<Int> = listOf(12, 8, 15, 6, 20, 4, 13) // Dummy data for Mon-Sun
)

class UsageDetailsViewModel : ViewModel() {

    private val _uiState = MutableLiveData<UsageDetailsUiState>()
    val uiState: LiveData<UsageDetailsUiState> = _uiState

    init {
        // Load initial data
        _uiState.value = UsageDetailsUiState()
    }
}