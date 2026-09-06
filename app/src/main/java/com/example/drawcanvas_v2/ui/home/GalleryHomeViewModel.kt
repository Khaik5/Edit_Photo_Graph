package com.example.drawcanvas_v2.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class GalleryHomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(GalleryHomeState())
    val state = _state.asStateFlow()
    private val _effect = Channel<GalleryHomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()
    fun onAction(action: GalleryHomeAction) {
        when (action) {
            is GalleryHomeAction.Start -> {
                start(action.hasPermission)
            }
            is GalleryHomeAction.PermissionResult -> {
                permissionResult(action.granted)
            }
            is GalleryHomeAction.TabChanged -> {
                changeTab(action.position)
            }
        }
    }
    private fun start(
        hasPermission: Boolean
    ) {
        _state.value = _state.value.copy(hasPermission = hasPermission)
        if (hasPermission) {
            _effect.trySend(GalleryHomeEffect.ShowGallery)
        } else {
            _effect.trySend(GalleryHomeEffect.RequestPermission)
        }
    }
    private fun permissionResult(granted: Boolean) {
        _state.value = _state.value.copy(hasPermission = granted)
        if (granted) {
            _effect.trySend(GalleryHomeEffect.ShowGallery)
        } else {
            _effect.trySend(GalleryHomeEffect.PermissionDenied)
        }
    }
    private fun changeTab(
        position: Int
    ) {
        _state.value = _state.value.copy(currentTab = position)
    }
}