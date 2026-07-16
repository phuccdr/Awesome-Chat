package com.rikkeisoft.awesome.custom

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.project.core.utils.dp
import com.rikkeisoft.awesome.adapter.message.MessageAdapter
import com.rikkeisoft.awesome.model.MessageItem
import com.rikkeisoft.awesome.model.MessagePosition

class ChatItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return
        val adapter = parent.adapter as? MessageAdapter ?: return
        val item = adapter.getItemAt(position)
        when (item) {
            is MessageItem.Message -> {
                if (item.messagePosition == MessagePosition.SINGLE || item.messagePosition == MessagePosition.BOTTOM) {
                    outRect.bottom = 16.dp()
                } else if (item.messagePosition == MessagePosition.MIDDLE || item.messagePosition == MessagePosition.TOP) {
                    outRect.bottom = 4.dp()
                }
            }

            is MessageItem.DateHeader -> {
                outRect.bottom = 28.dp()
            }
        }
    }
}