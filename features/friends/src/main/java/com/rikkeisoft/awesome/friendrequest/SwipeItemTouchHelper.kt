package com.rikkeisoft.awesome.friendrequest

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class SwipeItemTouchHelper : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // Only allow swiping for ViewHolders that implement SwipeRevealHolder
        return if (viewHolder is SwipeRevealHolder) {
            makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        } else {
            makeMovementFlags(0, 0)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // No-op: We handle the reveal in onChildDraw
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 2.0f

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float = Float.MAX_VALUE

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && viewHolder is SwipeRevealHolder) {
            Timber.tag("Swipe123").d("dX: $dX")
            val holder = viewHolder as SwipeRevealHolder
            val actionWidth = holder.actionWidth
            val contentView = holder.contentView

            val adapter = recyclerView.adapter as? ReceivedFriendRequestAdapter
            val isExpanded = adapter?.openedViewHolder == viewHolder

            val translationX = if (isCurrentlyActive) {
                if (!isExpanded) {
                    dX.coerceIn(-actionWidth, 0f)
                } else {
                    (dX - actionWidth).coerceIn(-actionWidth, 0f)
                }
            } else {
                val currentTx = contentView.translationX
                if (!isExpanded) {
                    if (currentTx < -actionWidth * 0.6f) -actionWidth else 0f
                } else {
                    if (currentTx > -actionWidth * 0.6f) 0f else -actionWidth
                }
            }

            contentView.translationX = translationX

            // Update adapter state
            if (translationX == -actionWidth) {
                adapter?.openedViewHolder = viewHolder
            } else if (translationX == 0f) {
                if (adapter?.openedViewHolder == viewHolder) {
                    adapter.openedViewHolder = null
                }
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder is SwipeRevealHolder) {
            val holder = viewHolder as SwipeRevealHolder
            val actionWidth = holder.actionWidth
            val contentView = holder.contentView
            
            // Final snap to ensure precision
            if (contentView.translationX <= -actionWidth * 0.5f) {
                contentView.translationX = -actionWidth
                (recyclerView.adapter as? ReceivedFriendRequestAdapter)?.openedViewHolder = viewHolder
            } else {
                contentView.translationX = 0f
                val adapter = recyclerView.adapter as? ReceivedFriendRequestAdapter
                if (adapter?.openedViewHolder == viewHolder) {
                    adapter.openedViewHolder = null
                }
            }
        }
    }
}
