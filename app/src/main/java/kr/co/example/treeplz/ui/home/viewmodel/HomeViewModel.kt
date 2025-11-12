package kr.co.example.treeplz.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class HomeUiState(
    val treeHealth: Int = 78,
    val requests: String = "8",
    val time: String = "19m",
    val tokens: String = "6.5k"
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    init {
        // Load initial data
        _uiState.value = HomeUiState()
    }
}