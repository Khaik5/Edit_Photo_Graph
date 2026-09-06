package com.example.drawcanvas_v2.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> LifecycleOwner.collectFlow(
    flow: Flow<T>,
    onCollect: (T) -> Unit
) {

    lifecycleScope.launch {

        flow
            .flowWithLifecycle(
                lifecycle,
                Lifecycle.State.STARTED
            )
            .collect { value ->

                onCollect(value)
            }
    }
}