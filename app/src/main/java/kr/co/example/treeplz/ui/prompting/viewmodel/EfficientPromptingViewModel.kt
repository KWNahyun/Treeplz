package kr.co.example.treeplz.ui.prompting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.example.treeplz.data.model.PromptTip

data class EfficientPromptingUiState(
    val tips: List<PromptTip> = emptyList()
)

class EfficientPromptingViewModel : ViewModel() {

    private val _uiState = MutableLiveData<EfficientPromptingUiState>()
    val uiState: LiveData<EfficientPromptingUiState> = _uiState

    init {
        loadPromptTips()
    }

    private fun loadPromptTips() {
        val tips = listOf(
            PromptTip("Remove Greetings", "Skip Social Pleasantries"),
            PromptTip("Be Specific", "Define Clear Requirements"),
            PromptTip("Define Output Format", "Specify Response Structure"),
            PromptTip("Set Constraints", "Use Word/Character Limits"),
            PromptTip("Reusable Templates", "Create Template Prompts"),
            PromptTip("Minimal Context", "Provide Only Necessary Context")
        )
        _uiState.value = EfficientPromptingUiState(tips)
    }
}