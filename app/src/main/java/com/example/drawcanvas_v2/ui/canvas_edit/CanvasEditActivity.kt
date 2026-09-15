package com.example.drawcanvas_v2.ui.canvas_edit

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.drawcanvas_v2.data.model.FrameItem
import com.example.drawcanvas_v2.data.model.StickerItem
import com.example.drawcanvas_v2.databinding.ActivityCanvasEditBinding
import com.example.drawcanvas_v2.ui.canvas_edit.adjustment.AdjustmentState
import com.example.drawcanvas_v2.ui.canvas_edit.adjustment.AdjustmentUi
import com.example.drawcanvas_v2.ui.canvas_edit.draw.DrawUi
import com.example.drawcanvas_v2.ui.canvas_edit.filter.FilterUi
import com.example.drawcanvas_v2.ui.canvas_edit.frame.FrameUi
import com.example.drawcanvas_v2.ui.canvas_edit.sticker.BitmapSticker
import com.example.drawcanvas_v2.ui.canvas_edit.sticker.StickerUi
import com.example.drawcanvas_v2.ui.canvas_edit.text.TextSticker
import com.example.drawcanvas_v2.ui.canvas_edit.text.TextUi
import com.example.drawcanvas_v2.ui.canvas_edit.ui.CanvasUi
import com.example.drawcanvas_v2.ui.canvas_edit.ui.MainToolUi
import com.example.drawcanvas_v2.utils.AssetUtils
import com.example.drawcanvas_v2.utils.CanvasResetUtils
import com.example.drawcanvas_v2.utils.CanvasSaveUtils
import com.example.drawcanvas_v2.utils.DialogUtils
import com.example.drawcanvas_v2.utils.ImageFilterUtils
import com.example.drawcanvas_v2.utils.ImageGeometryUtils
import com.example.drawcanvas_v2.utils.InsetsUtils
import com.example.drawcanvas_v2.utils.collectFlow
import android.graphics.Bitmap
import com.example.drawcanvas_v2.ui.canvas_edit.crop.CropAction
import com.example.drawcanvas_v2.ui.canvas_edit.crop.CropController
import com.example.drawcanvas_v2.ui.canvas_edit.crop.CropUi
import com.example.drawcanvas_v2.ui.canvas_edit.crop.CropViewModel

class CanvasEditActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCanvasEditBinding.inflate(layoutInflater) }
    private val viewModel: CanvasEditViewModel by viewModels()
    private var imageUri: Uri? = null
    private val cropViewModel: CropViewModel by viewModels()
    private var lastMode = EditorMode.MAIN
    private var cropSourceBitmap: Bitmap? = null
    private val canvasUi by lazy { CanvasUi(binding) }
    private val cropController by lazy { CropController(binding)}
    private val mainToolUi by lazy {
        MainToolUi(binding) { tool ->
            viewModel.onAction(CanvasEditAction.ToolClick(tool))
        }
    }
    private val drawUi by lazy {
        DrawUi(binding.panelDraw,binding.drawView,viewModel)
    }

    private val textUi by lazy {
        TextUi(binding.panelText, binding.stickerView, viewModel)
    }
    private val stickerUi by lazy { StickerUi(binding.panelSticker) { addSticker(it) }
    }
    private val frameUi by lazy {
        FrameUi(binding.panelFrame) {
            applyFrame(it)
        }
    }

    private val adjustmentUi by lazy {
        AdjustmentUi(binding.panelAdjustment, viewModel)
    }
    private val cropUi by lazy {
        CropUi(binding.panelCrop, binding.cropImageView, cropViewModel)
    }
    private var filterUi: FilterUi? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupInsets()
        imageUri = intent.getStringExtra(IMAGE_URI)?.let(Uri::parse)
        setupImage()
        setupUi()
        setupListeners()
        setupStickerCallbacks()
        observe()
    }

    override fun onDestroy() {
        cropController.release()
        super.onDestroy()
    }
    private fun setupInsets() {
        InsetsUtils.apply(this, binding.root)
    }
//    private fun readImage() {
//        imageUri = intent.getStringExtra(IMAGE_URI)?.let(Uri::parse)
//    }
    private fun setupImage() {
        val uri = imageUri ?: return
        Glide.with(this)
            .load(uri)
            .placeholder(android.R.color.transparent)
            .error(android.R.color.transparent).fitCenter().into(binding.ivImage)
    }
    private fun setupUi() {
        mainToolUi.setup()
        drawUi.setup()
        textUi.setup()
        stickerUi.setup()
        frameUi.setup()
        adjustmentUi.setup()
        cropUi.setup()
        imageUri?.let { uri ->
            filterUi = FilterUi(binding.panelFilter, uri, viewModel)
            filterUi?.setup()
        }

    }
    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            viewModel.onAction(CanvasEditAction.Back)
        }
        binding.btnSave.setOnClickListener {
            viewModel.onAction(CanvasEditAction.Save)
        }
        binding.btnToolReset.setOnClickListener {
            resetCurrentTool()
        }
        binding.btnToolBack.setOnClickListener {
                if (
                    viewModel.state.value.mode ==
                    EditorMode.CROP
                ) {
                    cancelCrop()
                } else {

                    viewModel.onAction(
                        CanvasEditAction.ToolBack
                    )
                }
            }
        binding.btnToolDone.setOnClickListener {
                if (
                    viewModel.state.value.mode ==
                    EditorMode.CROP
                ) {
                    applyCrop()
                } else {
                    viewModel.onAction(
                        CanvasEditAction.ToolDone
                    )
                }
            }
        onBackPressedDispatcher
            .addCallback(this) {
                if (
                    viewModel.state.value.mode ==
                    EditorMode.CROP
                ) {
                    cancelCrop()
                } else {
                    viewModel.onAction(
                        CanvasEditAction.Back
                    )
                }
            }
    }
    private fun setupStickerCallbacks() {
        binding.stickerView.onStickerSelected = { sticker ->
            viewModel.setSelectedText(sticker as? TextSticker)
        }

        binding.stickerView.onStickerDoubleClick = { sticker ->
            if (sticker is TextSticker) editText(sticker)
        }
    }

    private fun editText(sticker: TextSticker, isNew: Boolean = false) {
        viewModel.setSelectedText(sticker)
        DialogUtils.textInput(
            context = this,
            initial = sticker.getText(),
            onResult = { text ->
                if (text.isNotBlank()) {
                    sticker.setText(text)
                    binding.stickerView.invalidate()
                    viewModel.onAction(CanvasEditAction.Changed)
                } else if (isNew) {
                    binding.stickerView.removeSticker(sticker)
                }
            },
            onCancel = {
                if (isNew) binding.stickerView.removeSticker(sticker)
            }
        )
    }
    private fun observe() {
        collectFlow(viewModel.state) { state ->
            canvasUi.show(state)
            drawUi.render(state)
            textUi.render(state)
            stickerUi.render(state)
            frameUi.render(state)
            adjustmentUi.render(state)
            filterUi?.render(state)
            renderImageEffect(state)
            handleMode(state.mode)
            setTouchInteraction(state.mode)
        }
        collectFlow(
            cropViewModel.state
        ) { state ->
            cropUi.render(state)
        }
        collectFlow(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }

    private fun setTouchInteraction(mode: EditorMode) {
        when (mode) {
            EditorMode.DRAW -> {
                binding.drawView.setInteractionEnabled(true)
                binding.stickerView.setInteractionEnabled(false)
                binding.stickerView.clearSelection()
            }
            EditorMode.TEXT -> {
                binding.drawView.setInteractionEnabled(false)
                binding.stickerView.setInteractionEnabled(true)
            }
            EditorMode.STICKER -> {
                binding.drawView.setInteractionEnabled(false)
                binding.stickerView.setInteractionEnabled(true)
            }
            else -> {
                binding.drawView.setInteractionEnabled(false)
                binding.stickerView.setInteractionEnabled(false)
            }
        }
    }
    private fun handleMode(mode: EditorMode) {
        if (mode == lastMode) return
        lastMode = mode
        when (mode) {
            EditorMode.CROP -> { enterCropMode() }
            EditorMode.STICKER -> viewModel.loadStickers(this)
            EditorMode.FRAME -> viewModel.loadFrames(this)
            EditorMode.TEXT -> addText()
            else -> Unit
        }
    }
    private fun enterCropMode() {
        binding.editorContainer.post {
            if (
                viewModel.state.value.mode !=
                EditorMode.CROP
            ) {
                return@post
            }
            val success = cropController.enter()
            if (!success) {
                Toast.makeText(
                    this,
                    "Unable to open crop",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.onAction(
                    CanvasEditAction.ToolBack
                )
                return@post
            }
            cropViewModel.onAction(
                CropAction.Reset
            )
        }
    }
    private fun applyCrop() {
        val bitmap =
            cropController.crop()
        if (bitmap == null) {

            Toast.makeText(
                this,
                "Crop failed",
                Toast.LENGTH_SHORT
            ).show()

            return
        }
        cropController.commit(
            bitmap
        )
        viewModel.setSelectedText(
            null
        )
        viewModel.onAction(
            CanvasEditAction.CropApplied
        )
        updateFrameRect()
    }
    private fun cancelCrop() {
        cropController.cancel()
        viewModel.onAction(
            CanvasEditAction.ToolBack
        )
    }

    private fun addText() {
        val sticker = TextSticker(this, "Enter Your Text")
        binding.stickerView.addSticker(sticker)
        editText(sticker, isNew = true)
    }

    private fun addSticker(item: StickerItem) {
        val bitmap = AssetUtils.loadBitmap(this, item.path) ?: return // load sticker
        binding.stickerView.addSticker(BitmapSticker(bitmap))
        viewModel.onAction(CanvasEditAction.Changed)
    }

    private fun applyFrame(item: FrameItem) {
        val bitmap = AssetUtils.loadBitmap(this, item.path) ?: return
        binding.frameOverlay.setFrame(bitmap)
        updateFrameRect()
        viewModel.onAction(CanvasEditAction.Changed)
    }

    private fun updateFrameRect() {
        binding.editorContainer.post {
            // hiển thị vùng của ảnh
            ImageGeometryUtils.getDisplayRect(binding.ivImage)?.let { rect ->
                //đặt frame vừa với vùng của ảnh
                binding.frameOverlay.setTargetRect(rect) // gọi bản sao
            }
        }
    }

    private fun renderImageEffect(state: CanvasEditState) {
        binding.ivImage.colorFilter = ImageFilterUtils.create(
                AdjustmentState(
                    selected = state.selectedAdjustment,
                    brightness = state.brightness,
                    contrast = state.contrast,
                    saturation = state.saturation,
                    hue = state.hue
                ),
                state.selectedFilter
            )
    }

    private fun resetCurrentTool() {
        when (viewModel.state.value.mode) {
            EditorMode.DRAW -> {
                drawUi.reset()
                viewModel.onAction(CanvasEditAction.ResetCurrentTool)
            }
            EditorMode.CROP -> {
                cropViewModel.onAction(CropAction.Reset)
            }
            EditorMode.TEXT -> {
                val textSticker = viewModel.getSelectedText()
                if (textSticker != null) {
                    textSticker.setTextColor(android.graphics.Color.WHITE)
                    textSticker.clearFont()
                    textSticker.setTextSize(36f)
                    textSticker.setStrokeWidth(0f)
                    binding.stickerView.invalidate()
                }
                viewModel.onAction(CanvasEditAction.ResetCurrentTool)
            }
            EditorMode.ADJUSTMENT -> {
                binding.ivImage.clearColorFilter()
                viewModel.onAction(CanvasEditAction.ResetCurrentTool)
            }
            EditorMode.FILTER -> {
                binding.ivImage.clearColorFilter()
                viewModel.onAction(CanvasEditAction.ResetCurrentTool)
            }
            EditorMode.STICKER -> {
                binding.stickerView.clearStickers()
                viewModel.onAction(CanvasEditAction.ResetCurrentTool)
                viewModel.onAction(CanvasEditAction.Changed)
            }
            EditorMode.FRAME -> {
                binding.frameOverlay.clearFrame()
                viewModel.onAction(CanvasEditAction.ResetCurrentTool)
                viewModel.onAction(CanvasEditAction.Changed)
            }
            EditorMode.MAIN -> Unit
        }
    }

    private fun handleEffect(effect: CanvasEditEffect) {
        when (effect) {
            CanvasEditEffect.SaveImage -> saveImage()
            CanvasEditEffect.RestoreImage -> restoreImage()
            CanvasEditEffect.ConfirmRestore -> confirmRestore()
            CanvasEditEffect.ConfirmExit -> confirmExit()
            CanvasEditEffect.Finish -> finish()
            is CanvasEditEffect.ShowMessage -> Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmRestore() {
        DialogUtils.confirm(this, "Restore", "Remove all current changes?") {
            viewModel.onAction(CanvasEditAction.RestoreConfirm)
        }
    }

    private fun confirmExit() {
        DialogUtils.confirm(this, "Exit editor", "Changes will not be saved.") {
            finish()
        }
    }

    private fun restoreImage() {
        cropController.cancel()
        cropController.clearCommittedBitmap()
        CanvasResetUtils.reset(binding)
        setupImage()
        lastMode = EditorMode.MAIN
    }

    private fun saveImage() {
        CanvasSaveUtils.save(this, binding) { success ->
            viewModel.onAction(CanvasEditAction.SaveResult(success))
        }
    }
    companion object {
        const val IMAGE_URI = "IMAGE_URI"
    }
}
