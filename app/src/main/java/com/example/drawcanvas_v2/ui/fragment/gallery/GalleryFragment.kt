package com.example.drawcanvas_v2.ui.fragment.gallery
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.drawcanvas_v2.data.model.GalleryType
import com.example.drawcanvas_v2.databinding.FragmentGalleryBinding
import com.example.drawcanvas_v2.ui.gallery.GalleryAction
import com.example.drawcanvas_v2.ui.gallery.GalleryAdapter
import com.example.drawcanvas_v2.ui.gallery.GalleryEffect
import com.example.drawcanvas_v2.ui.gallery.GalleryState
import com.example.drawcanvas_v2.ui.gallery.GalleryViewModel
import com.example.drawcanvas_v2.ui.preview.EditActivity
import com.example.drawcanvas_v2.utils.collectFlow

class GalleryFragment :
    Fragment() {
    private val binding by lazy { FragmentGalleryBinding.inflate(layoutInflater) }
    private val viewModel: GalleryViewModel by viewModels()
    private val adapter by lazy {

        GalleryAdapter { item ->
            viewModel.onAction(GalleryAction.ImageClick(item.image.toString()))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        setupRecyclerView()
        observeViewModel()
        loadImages()
    }
    private fun setupRecyclerView() {
        binding.rvImages.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvImages.adapter = adapter
    }
    private fun loadImages() {
        val type =
            getGalleryType()
        viewModel.onAction(GalleryAction.Load(type)
        )
    }
    private fun getGalleryType():
            GalleryType {
        val value = requireArguments().getString(TYPE)
        return GalleryType.valueOf(value ?: GalleryType.RECENTS.name)
    }
    private fun observeViewModel() {

        viewLifecycleOwner.collectFlow(
            viewModel.state
        ) { state ->

            showState(
                state
            )
        }

        viewLifecycleOwner.collectFlow(
            viewModel.effect
        ) { effect ->

            handleEffect(
                effect
            )
        }
    }

    private fun showState(
        state: GalleryState
    ) {
        binding.progressBar.isVisible = state.isLoading
        binding.tvEmpty.isVisible = !state.isLoading && state.images.isEmpty()
        binding.rvImages.isVisible = !state.isLoading && state.images.isNotEmpty()
        adapter.setItems(state.images)
    }

    private fun handleEffect(
        effect: GalleryEffect
    ) {

        when (effect) {

            is GalleryEffect.OpenPreview -> {
                openPreview(
                    effect.uri
                )
            }

            is GalleryEffect.ShowMessage -> {
                Toast.makeText(
                    requireContext(),
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openPreview(
        uri: String
    ) {
        val intent = Intent(requireContext(), EditActivity::class.java)
        intent.putExtra(EditActivity.IMAGE_URI, uri
        )
        startActivity(intent)
    }
    companion object {
        private const val TYPE = "TYPE"
        fun newInstance(
            type: GalleryType
        ): GalleryFragment {
            return GalleryFragment().apply {
                    arguments = bundleOf().apply {
                        putString(TYPE, type.name)
                    }
                }
        }
    }
}