package kr.co.example.treeplz.ui.prompting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.example.treeplz.data.model.PromptTip
import kr.co.example.treeplz.databinding.ItemPromptTipBinding

class PromptTipAdapter(private val tips: List<PromptTip>) : RecyclerView.Adapter<PromptTipAdapter.PromptTipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromptTipViewHolder {
        val binding = ItemPromptTipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromptTipViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromptTipViewHolder, position: Int) {
        holder.bind(tips[position])
    }

    override fun getItemCount() = tips.size

    class PromptTipViewHolder(private val binding: ItemPromptTipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tip: PromptTip) {
            binding.tipTitle.text = tip.title
            binding.tipDescription.text = tip.description
        }
    }
}