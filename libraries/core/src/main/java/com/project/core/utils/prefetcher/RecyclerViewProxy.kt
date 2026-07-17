@file:Suppress("PackageDirectoryMismatch")

package androidx.recyclerview.widget

import com.project.core.utils.prefetcher.PrefetchViewPool

internal fun RecyclerView.RecycledViewPool.attachToPreventFromClearingxoa123() {
    attach()
}

internal fun RecyclerView.ViewHolder.setItemViewType(viewType: Int) {
    mItemViewType = viewType
}

internal fun PrefetchViewPool.factorInCreateTimexoa123(viewType: Int, creationTimeNanos: Long) {
    (this as RecyclerView.RecycledViewPool).factorInCreateTime(viewType, creationTimeNanos)
}