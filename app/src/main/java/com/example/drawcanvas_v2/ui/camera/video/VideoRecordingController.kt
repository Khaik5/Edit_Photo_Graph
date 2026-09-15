package com.example.drawcanvas_v2.ui.camera.video

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat

class VideoRecordingController {
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null

    fun create(rotation: Int): VideoCapture<Recorder> {
        val recorder = Recorder.Builder()
            .setQualitySelector(
                QualitySelector.from(
                    Quality.FHD,
                    FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
                )
            )
            .build()
        return VideoCapture.withOutput(recorder)
            .also {
                it.targetRotation = rotation
                videoCapture = it
            }
    }

    fun start(context: Context, withAudio: Boolean, onEvent: (VideoRecordingEvent) -> Unit) {
        val capture = videoCapture ?: run {
            onEvent(VideoRecordingEvent.Failed("Video capture is not ready"))
            return
        }
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "DrawCanvas_${System.currentTimeMillis()}")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/DrawCanvas")
            }
        }
        val output = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(values).build()
        try {
            var pending = capture.output.prepareRecording(context, output)
            if (
                withAudio &&
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pending = pending.withAudioEnabled()
            }
            activeRecording = pending.start(ContextCompat.getMainExecutor(context)) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> onEvent(VideoRecordingEvent.Started)
                    is VideoRecordEvent.Status -> onEvent(
                        VideoRecordingEvent.DurationChanged(
                            event.recordingStats.recordedDurationNanos / NANOS_PER_MILLISECOND // thgian quay video
                        )
                    )
                    is VideoRecordEvent.Finalize -> {
                        activeRecording = null
                        if (event.hasError()) {
                            onEvent(VideoRecordingEvent.Failed("Cannot save video (${event.error})"))
                        } else {
                            onEvent(VideoRecordingEvent.Finalized(event.outputResults.outputUri.toString()))
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            onEvent(VideoRecordingEvent.Failed("Cannot start video recording"))
        }
    }

    fun stop(onNotRecording: () -> Unit) {
        activeRecording?.stop() ?: onNotRecording()
    }

    fun release() {
        activeRecording?.stop()
        activeRecording = null
        videoCapture = null
    }

    private companion object {
        const val NANOS_PER_MILLISECOND = 1_000_000L
    }
}

sealed interface VideoRecordingEvent {
    data object Started : VideoRecordingEvent
    data class DurationChanged(val durationMillis: Long) : VideoRecordingEvent
    data class Finalized(val outputUri: String) : VideoRecordingEvent
    data class Failed(val message: String) : VideoRecordingEvent
}
