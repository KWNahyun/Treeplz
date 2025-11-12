package kr.co.example.treeplz.ui.details

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kr.co.example.treeplz.databinding.FragmentUsageDetailsBinding
import kr.co.example.treeplz.ui.details.viewmodel.UsageDetailsViewModel

class UsageDetailsFragment : Fragment() {

    private var _binding: FragmentUsageDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UsageDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsageDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            binding.aiRequestsValue.text = uiState.aiRequests
            binding.timeSpentValue.text = uiState.timeSpent
            binding.tokensUsedValue.text = uiState.tokensUsed
            binding.carbonFootprintValue.text = uiState.carbonFootprint

            setupBarChart(binding.barChart, uiState.weeklyTrend)
        }
    }

    private fun setupBarChart(barChart: BarChart, weeklyTrend: List<Int>) {
        val entries = weeklyTrend.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Weekly Carbon Footprint")
        dataSet.color = Color.parseColor("#4CAF50")
        dataSet.setDrawValues(false)

        val barData = BarData(dataSet)
        barChart.data = barData

        // Customize X-axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))

        // Customize Y-axis
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false

        // General Chart Settings
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setTouchEnabled(false)

        barChart.invalidate() // Refresh the chart
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}