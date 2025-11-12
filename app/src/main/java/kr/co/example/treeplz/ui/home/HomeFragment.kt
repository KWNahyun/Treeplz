package kr.co.example.treeplz.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kr.co.example.treeplz.R
import kr.co.example.treeplz.databinding.FragmentHomeBinding
import kr.co.example.treeplz.ui.home.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        observeViewModel()
    }

    private fun setupNavigation() {
        binding.detailedUsageButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_usageDetailsFragment)
        }

        binding.efficientPromptingButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_efficientPromptingFragment)
        }

        binding.calendarIcon.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_usageCalendarFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            binding.healthPercentage.text = "${uiState.treeHealth}%"
            binding.healthProgressBar.progress = uiState.treeHealth
            updateTreeImage(uiState.treeHealth)
        }
    }

    private fun updateTreeImage(healthPercentage: Int) {
        val treeImageResource = when {
            healthPercentage >= 84 -> R.drawable.tree_state_1
            healthPercentage >= 67 -> R.drawable.tree_state_2
            healthPercentage >= 50 -> R.drawable.tree_state_3
            healthPercentage >= 33 -> R.drawable.tree_state_4
            healthPercentage >= 16 -> R.drawable.tree_state_5
            else -> R.drawable.tree_state_6
        }
        binding.treeImage.setImageResource(treeImageResource)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}