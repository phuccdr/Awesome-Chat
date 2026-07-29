package com.rikkeisoft.awesome.friendrequest

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeItemTouchHelper(
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // We don't want to actually swipe the item away, just reveal the button
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return Float.MAX_VALUE
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return Float.MAX_VALUE
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val binding = (viewHolder as? ReceivedFriendRequestAdapter.FriendRequestViewHolder)?.binding
            binding?.let {
                val actionWidth = it.layoutAction.width.toFloat()
                val currentTranslationX = it.layoutContent.translationX

                val translationX = if (isCurrentlyActive) {
                    // Limit swipe distance to the width of the action layout
                    if (dX < -actionWidth) -actionWidth else dX
                } else {
                    // When user releases finger, ItemTouchHelper animates dX back to 0.
                    // If it was swiped past 80%, we keep it open at -actionWidth.
                    if (currentTranslationX <= -actionWidth * 0.8f) {
                        -actionWidth
                    } else {
                        dX
                    }
                }
                it.layoutContent.translationX = translationX
            }
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val binding = (viewHolder as? ReceivedFriendRequestAdapter.FriendRequestViewHolder)?.binding
        binding?.let {
            val actionWidth = it.layoutAction.width.toFloat()
            // Snap behavior with 80% threshold
            if (it.layoutContent.translationX <= -actionWidth * 0.8f) {
                it.layoutContent.translationX = -actionWidth
            } else {
                it.layoutContent.translationX = 0f
            }
        }
    }
}
