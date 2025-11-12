package kr.co.example.treeplz.ui.prompting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kr.co.example.treeplz.databinding.FragmentEfficientPromptingBinding
import kr.co.example.treeplz.ui.prompting.viewmodel.EfficientPromptingViewModel

class EfficientPromptingFragment : Fragment() {

    private var _binding: FragmentEfficientPromptingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EfficientPromptingViewModel by viewModels()

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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.tipsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            binding.tipsRecyclerView.adapter = PromptTipAdapter(uiState.tips)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}