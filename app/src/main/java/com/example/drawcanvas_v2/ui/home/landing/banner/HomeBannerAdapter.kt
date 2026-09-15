package com.example.drawcanvas_v2.ui.home.landing.banner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.drawcanvas_v2.data.model.HomeBannerItem
import com.example.drawcanvas_v2.databinding.ItemHomeBannerBinding

class HomeBannerAdapter(
    private val items: List<HomeBannerItem>
) : RecyclerView.Adapter<HomeBannerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeBannerBinding.inflate(
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
        private val binding: ItemHomeBannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeBannerItem) {
            binding.bannerSurface.setBackgroundResource(item.backgroundRes)
            binding.tvTitle.setText(item.titleRes)
            binding.tvDescription.setText(item.descriptionRes)
            binding.btnAction.setText(item.buttonTextRes)
            binding.btnAction.icon = ContextCompat.getDrawable(binding.root.context, item.imageRes)
            binding.ivTool.setImageResource(item.imageRes)
        }
    }
}
