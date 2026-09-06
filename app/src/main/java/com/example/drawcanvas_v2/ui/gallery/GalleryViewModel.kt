package com.example.drawcanvas_v2.ui.gallery
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawcanvas_v2.data.model.GalleryType
import com.example.drawcanvas_v2.data.model.ImageItem
import com.example.drawcanvas_v2.data.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(
    application: Application
) : AndroidViewModel(
    application
) {
    private val repository = ImageRepository()
    private val _state = MutableStateFlow(GalleryState())
    val state = _state.asStateFlow()
    private val _effect = Channel<GalleryEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    fun onAction(
        action: GalleryAction
    ) {
        when (action) {
            is GalleryAction.Load -> {
                loadImages(action.type)
            }
            is GalleryAction.ImageClick -> {
                openImage(action.uri)
            }
        }
    }
    private fun loadImages(
        type: GalleryType
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val images = withContext(Dispatchers.IO) {
                    repository.getImages(getApplication<Application>().contentResolver) }
                val result = filterImages(
                        images,
                        type
                    )
                _state.value = GalleryState(isLoading = false, images = result)
            } catch (
                exception: Exception
            ) {
                _state.value = GalleryState()
                _effect.send(GalleryEffect.ShowMessage("Cannot load images"
                    )
                )
            }
        }
    }
    private fun filterImages(
        images: List<ImageItem>,
        type: GalleryType
    ): List<ImageItem> {
        return when (type) {
            GalleryType.RECENTS -> {
                images
            }
            GalleryType.FAVOURITES -> {
                images.filter { item ->
                    item.favourite
                }
            }
            GalleryType.SELFIES -> {
                images.filter { item ->
                    item.selfie
                }
            }
        }
    }
    private fun openImage(
        uri: String
    ) {
        _effect.trySend(GalleryEffect.OpenPreview(uri)
        )
    }
}