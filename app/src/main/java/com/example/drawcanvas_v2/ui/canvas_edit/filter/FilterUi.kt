package com.example.drawcanvas_v2.ui.canvas_edit.filter

import android.net.Uri
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawcanvas_v2.databinding.PanelFilterBinding
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditAction
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditState
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditViewModel
import com.example.drawcanvas_v2.utils.AnimationUtils

class FilterUi(
    private val binding: PanelFilterBinding,
    private val imageUri: Uri,
    private val viewModel: CanvasEditViewModel
) {

    private val adapter =
        FilterAdapter(
            imageUri
        ) { filter ->
            viewModel.onAction(
                CanvasEditAction.FilterSelected(filter)
            )
            viewModel.onAction(
                CanvasEditAction.Changed
            )
        }

    fun setup() {
        binding.rvFilters.layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFilters.adapter = adapter
    }

    fun render(
        state: CanvasEditState
    ) {
        adapter.select(state.selectedFilter)
        binding.rvFilters.post {
            AnimationUtils.animateItems(binding.rvFilters)
        }
    }
}