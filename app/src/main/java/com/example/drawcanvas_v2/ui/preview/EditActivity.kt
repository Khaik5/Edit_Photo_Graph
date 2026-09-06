package com.example.drawcanvas_v2.ui.preview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.drawcanvas_v2.databinding.ActivityEditBinding
import com.example.drawcanvas_v2.ui.canvas_edit.CanvasEditActivity
import com.example.drawcanvas_v2.utils.InsetsUtils
import com.example.drawcanvas_v2.utils.collectFlow

class EditActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel:
            PreviewViewModel by viewModels()

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )
        setContentView(
            binding.root
        )

        setupInsets()
        setupListeners()
        observe()
        loadImage()
    }

    private fun setupInsets() {

        InsetsUtils.apply(
            this,
            binding.root
        )
    }

    private fun setupListeners() {

        binding.btnBack.setOnClickListener {

            viewModel.onAction(
                PreviewAction.Back
            )
        }

        binding.btnEdit.setOnClickListener {
            viewModel.onAction(
                PreviewAction.Edit
            )
        }
    }

    private fun loadImage() {

        val uri =
            intent.getStringExtra(
                IMAGE_URI
            )

        viewModel.onAction(
            PreviewAction.Load(
                uri
            )
        )
    }

    private fun observe() {

        collectFlow(
            viewModel.state
        ) { state ->

            showState(
                state
            )
        }

        collectFlow(
            viewModel.effect
        ) { effect ->

            handleEffect(
                effect
            )
        }
    }

    private fun showState(
        state: PreviewState
    ) {

        val uri =
            state.imageUri
                ?: return

        Glide.with(this)
            .load(Uri.parse(uri))
            .fitCenter()
            .into(binding.ivImage)
    }

    private fun handleEffect(
        effect: PreviewEffect
    ) {

        when (effect) {

            PreviewEffect.Finish -> {
                finish()
            }

            is PreviewEffect.OpenEditor -> {

                openEditor(
                    effect.imageUri
                )
            }
        }
    }

    private fun openEditor(
        uri: String
    ) {

        val intent =
            Intent(
                this,
                CanvasEditActivity::class.java
            )

        intent.putExtra(
            CanvasEditActivity.IMAGE_URI,
            uri
        )

        startActivity(
            intent
        )
    }

    companion object {

        const val IMAGE_URI =
            "IMAGE_URI"
    }
}