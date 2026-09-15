package com.example.drawcanvas_v2.ui.home.landing.promotion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.data.model.HomePromotionItem
import com.example.drawcanvas_v2.databinding.ItemHomePromotionBinding

class HomePromotionAdapter(
    private val items: List<HomePromotionItem>
) : RecyclerView.Adapter<HomePromotionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomePromotionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(
        private val binding: ItemHomePromotionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomePromotionItem) {
            binding.promotionSurface.setBackgroundResource(item.backgroundRes)
            binding.tvTitle.setText(item.titleRes)
            binding.tvSubtitle.setText(item.subtitleRes)
            binding.btnAction.setText(item.buttonTextRes)
            binding.btnAction.icon = ContextCompat.getDrawable(binding.root.context, item.actionIconRes)
            binding.ivPromotion.setImageResource(item.imageRes)
            binding.ivPromotion.isVisible = item.ratios.isEmpty()
            binding.ratioContainer.isVisible = item.ratios.isNotEmpty()
            item.ratios.getOrNull(0)?.let(binding.tvRatioOne::setText)
            item.ratios.getOrNull(1)?.let(binding.tvRatioTwo::setText)
            item.ratios.getOrNull(2)?.let(binding.tvRatioThree::setText)
        }
    }
}
