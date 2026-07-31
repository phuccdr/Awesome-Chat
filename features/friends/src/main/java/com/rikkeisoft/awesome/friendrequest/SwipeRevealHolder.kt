package com.rikkeisoft.awesome.friendrequest

import android.view.View

/**
 * Interface to be implemented by [androidx.recyclerview.widget.RecyclerView.ViewHolder]
 * to support swipe-to-reveal functionality.
 */
interface SwipeRevealHolder {
    /**
     * The view that will be translated during the swipe.
     */
    val contentView: View

    /**
     * The maximum width of the revealable action area.
     */
    val actionWidth: Float
}
