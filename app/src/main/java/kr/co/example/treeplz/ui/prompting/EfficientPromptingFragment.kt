package kr.co.example.treeplz.ui.prompting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.example.treeplz.data.model.PromptTip
import kr.co.example.treeplz.databinding.FragmentEfficientPromptingBinding

class EfficientPromptingFragment : Fragment() {

    private var _binding: FragmentEfficientPromptingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEfficientPromptingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val tips = listOf(
            PromptTip("Remove Greetings", "Skip Social Pleasantries"),
            PromptTip("Be Specific", "Define Clear Requirements"),
            PromptTip("Define Output Format", "Specify Response Structure"),
            PromptTip("Set Constraints", "Use Word/Character Limits"),
            PromptTip("Reusable Templates", "Create Template Prompts"),
            PromptTip("Minimal Context", "Provide Only Necessary Context")
        )

        val adapter = PromptTipAdapter(tips)
        binding.tipsRecyclerView.adapter = adapter
        binding.tipsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}