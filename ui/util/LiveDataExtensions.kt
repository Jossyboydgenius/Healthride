package com.example.healthride.ui.util

import androidx.compose.runtime.*
import androidx.lifecycle.LiveData

@Composable
fun <T> LiveData<T>.observeAsState(initial: T? = null): State<T?> {
    val state = remember { mutableStateOf(initial) }

    DisposableEffect(this) {
        val observer = androidx.lifecycle.Observer<T> { value ->
            state.value = value
        }
        this@observeAsState.observeForever(observer)

        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }

    return state
}