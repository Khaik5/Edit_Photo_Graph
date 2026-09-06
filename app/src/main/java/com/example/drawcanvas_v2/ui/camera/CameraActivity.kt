package com.example.drawcanvas_v2.ui.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.drawcanvas_v2.databinding.ActivityCameraBinding
import com.example.drawcanvas_v2.ui.camera.camera.CameraSessionController
import com.example.drawcanvas_v2.ui.camera.photo.PhotoCaptureController
import com.example.drawcanvas_v2.ui.camera.ui.CameraUiController
import com.example.drawcanvas_v2.ui.camera.video.VideoRecordingController
import com.example.drawcanvas_v2.ui.camera.video.VideoRecordingEvent
import com.example.drawcanvas_v2.ui.preview.EditActivity
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCameraBinding.inflate(layoutInflater) }
    private val viewModel: CameraViewModel by viewModels()
    private val cameraSession by lazy { CameraSessionController(this) }
    private val photoCapture = PhotoCaptureController()
    private val videoRecording = VideoRecordingController()
    private val cameraUi by lazy { CameraUiController(binding) }
    private lateinit var cameraExecutor: ExecutorService

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera() else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    private val audioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.onAction(CameraAction.AudioPermissionResult(granted))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraUi.bindActions(
            onAction = viewModel::onAction,
            onClose = ::finishCamera,
            stateProvider = { viewModel.state.value },
            exposureRangeProvider = cameraSession::exposureRange
        )
        observeViewModel()
        requestCameraPermissionIfNeeded()
    }

    override fun onDestroy() {
        videoRecording.release()
        cameraSession.release()
        if (::cameraExecutor.isInitialized) cameraExecutor.shutdown()
        super.onDestroy()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                cameraUi.render(state, cameraSession.hasFlashUnit(), cameraSession.exposureRange())
            }
        }
        lifecycleScope.launch { viewModel.effect.collect(::handleEffect) }
    }

    private fun handleEffect(effect: CameraEffect) {
        when (effect) {
            CameraEffect.RebindCamera -> bindCameraUseCases(viewModel.state.value)
            CameraEffect.CapturePhoto -> capturePhoto()
            CameraEffect.RequestAudioPermissionForVideo -> requestAudioPermissionForVideo()
            is CameraEffect.StartVideoRecording -> startVideoRecording(effect.withAudio)
            CameraEffect.StopVideoRecording -> {
                videoRecording.stop {
                    viewModel.onAction(CameraAction.VideoRecordingFailed("Video recording is not active"))
                }
            }
            is CameraEffect.SetZoomRatio -> cameraSession.setZoomRatio(effect.ratio)
            is CameraEffect.SetExposure -> cameraSession.setExposure(effect.index)
            is CameraEffect.SetFlash -> applyFlash(effect.state, effect.captureMode)
            is CameraEffect.OpenPhotoPreview -> openPreview(effect.imageUri)
            is CameraEffect.ShowMessage -> Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestCameraPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun requestAudioPermissionForVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onAction(CameraAction.AudioPermissionResult(true))
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startCamera() {
        cameraSession.start { binding.previewView.post { bindCameraUseCases(viewModel.state.value) } }
    }

    private fun bindCameraUseCases(state: CameraState) {
        if (state.isRecordingLocked) return
        val rotation = binding.previewView.display?.rotation ?: Surface.ROTATION_0
        val captureUseCase = when (state.captureMode) {
            CaptureMode.PHOTO -> photoCapture.create(rotation, state)
            CaptureMode.VIDEO -> videoRecording.create(rotation)
        }
        cameraSession.bind(
            owner = this,
            state = state,
            previewView = binding.previewView,
            faceOverlayView = binding.faceOverlayView,
            analyzerExecutor = cameraExecutor,
            captureUseCase = captureUseCase,
            onZoomStateChanged = { ratio, min, max ->
                viewModel.onAction(CameraAction.ZoomStateChanged(ratio, min, max))
            },
            onBound = {
                applyFlash(state.flashState, state.captureMode)
                cameraSession.setExposure(state.brightness)
                cameraUi.render(state, cameraSession.hasFlashUnit(), cameraSession.exposureRange())
            }
        )
    }

    private fun applyFlash(flashState: FlashState, captureMode: CaptureMode) {
        if (captureMode == CaptureMode.PHOTO) {
            photoCapture.setFlash(flashState)
            cameraSession.setTorch(false)
        } else {
            cameraSession.setTorch(flashState == FlashState.ON)
        }
        cameraUi.render(
            viewModel.state.value,
            cameraSession.hasFlashUnit(),
            cameraSession.exposureRange()
        )
    }

    private fun capturePhoto() {
        photoCapture.capture(
            context = this,
            state = viewModel.state.value,
            executor = cameraExecutor,
            scope = lifecycleScope,
            onPhotoReady = { viewModel.onAction(CameraAction.PhotoProcessed(it)) },
            onFailure = { viewModel.onAction(CameraAction.PhotoProcessingFailed(it)) }
        )
    }

    private fun startVideoRecording(withAudio: Boolean) {
        videoRecording.start(this, withAudio) { event ->
            when (event) {
                VideoRecordingEvent.Started -> viewModel.onAction(CameraAction.VideoRecordingStarted)
                is VideoRecordingEvent.DurationChanged -> {
                    viewModel.onAction(CameraAction.VideoDurationChanged(event.durationMillis))
                }
                is VideoRecordingEvent.Finalized -> {
                    viewModel.onAction(CameraAction.VideoRecordingFinalized(event.outputUri))
                }
                is VideoRecordingEvent.Failed -> viewModel.onAction(CameraAction.VideoRecordingFailed(event.message))
            }
        }
    }

    private fun finishCamera() {
        videoRecording.release()
        finish()
    }

    private fun openPreview(imageUri: String) {
        startActivity(Intent(this, EditActivity::class.java).apply {
            putExtra(EditActivity.IMAGE_URI, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        })
    }
}
