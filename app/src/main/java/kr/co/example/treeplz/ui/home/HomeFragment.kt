package kr.co.example.treeplz.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kr.co.example.treeplz.R
import kr.co.example.treeplz.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailedUsageButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_usageDetailsFragment)
        }

        binding.efficientPromptingButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_efficientPromptingFragment)
        }

        // TODO: Observe ViewModel and update tree image
        updateTreeImage(78) // Example call with 78% health
    }

    private fun updateTreeImage(healthPercentage: Int) {
        val treeImageResource = when {
            healthPercentage > 80 -> R.drawable.tree_state_0
            healthPercentage > 60 -> R.drawable.tree_state_1
            healthPercentage > 40 -> R.drawable.tree_state_2
            healthPercentage > 20 -> R.drawable.tree_state_3
            healthPercentage > 0 -> R.drawable.tree_state_4
            else -> R.drawable.tree_state_5
        }
        binding.treeImage.setImageResource(treeImageResource)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}