package com.project.core.utils.prefetcher

import androidx.recyclerview.widget.RecyclerView

inline val RecyclerView.defaultViewHolderProducer: ViewHolderProducerxoa123
    get() {
        val adapter = requireNotNull(adapter) { "You have to set RecyclerView's adapter first" }
        return adapter::createViewHolder
    }

fun RecyclerView.setupWithPrefetchViewPoolxoa123(
    defaultMaxRecycledViews: Int = PrefetchViewPool.DEFAULT_MAX_RECYCLED_VIEWS,
    viewHolderProducer: ViewHolderProducerxoa123 = defaultViewHolderProducer,
    builder: PrefetchViewPool.() -> Unit = {}
): PrefetchViewPool {
    val viewHolderSupplier = CoroutinesViewHolderSupplier(context, viewHolderProducer)
    val prefetchViewPool =
        PrefetchViewPool(defaultMaxRecycledViews, viewHolderSupplier).apply(builder)
    setRecycledViewPool(prefetchViewPool)
    return prefetchViewPool
}