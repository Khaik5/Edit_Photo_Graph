package com.example.drawcanvas_v2.ui.canvas_edit.frame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawcanvas_v2.databinding.ItemFrameBinding
import com.example.drawcanvas_v2.data.model.FrameItem
import com.example.drawcanvas_v2.utils.AnimationUtils

class FrameAdapter(
    private val onClick: (FrameItem) -> Unit
) : RecyclerView.Adapter<FrameAdapter.ViewHolder>() {
    private val items = mutableListOf<FrameItem>()
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            ItemFrameBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.bind(
            items[position],
            position == selectedPosition
        )
    }

    override fun getItemCount() =
        items.size

    fun submitList(
        list: List<FrameItem>
    ) {
        items.clear() // xóa ds cu
        items.addAll(list) //thêm danh sách mới
        selectedPosition = RecyclerView.NO_POSITION // reset vị trí đã chọn
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemFrameBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: FrameItem,
            selected: Boolean
        ) {
            //load ảnh frame từ asset
            Glide.with(binding.ivFrame)
                .load(
                    "file:///android_asset/${item.path}" //đường dẫn của ảnh của frame ừ asset
                )
                .centerCrop()
                .into(binding.ivFrame)
            // view khi mà mình chọn vào frame bất kì
            binding.viewSelected.visibility =
                if (selected) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            AnimationUtils.selected(binding.root, selected)
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition // lấy vị trí hiện tại của position
                if (position == RecyclerView.NO_POSITION) { // kiểm tra thử là vị trí có hợp lệ hay chưa
                    return@setOnClickListener
                }
                val old = selectedPosition
                selectedPosition = position // cập nhật vị trí mới nhất
                if (old != RecyclerView.NO_POSITION) {
                    notifyItemChanged(old) // vẽ lại item cũ khi mà bỏ chọn item cũ
                }
                notifyItemChanged(position) // sau đó vẽ lại cái mới nhất
                onClick(items[position])
            }
        }
    }
}