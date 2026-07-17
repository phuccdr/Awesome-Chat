package com.project.core.utils.prefetcher

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.RecycledViewPool.bindToLifecyclexoa123(lifecycleOwner: LifecycleOwner) {
    val observer = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            this@bindToLifecyclexoa123.clear()
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
}