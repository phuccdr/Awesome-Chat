package com.rikkeisoft.awesome.custom

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.project.core.utils.resource.ResourceUtils
import com.rikkeisoft.awesome.model.MessagePosition

class MessageChatTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private val LARGE_RADIUS: Float = 30F
    private val SMALL_RADIUS: Float = 2F
    private val backgroundReceiveMessageColor: Int =
        ResourceUtils.getColor(com.project.core.R.color.background_color_message)
    private val backgroundSentMessageColor: Int =
        ResourceUtils.getColor(com.project.core.R.color.primary_color)
    private val textReceivedMessageColor =
        ResourceUtils.getColor(com.project.core.R.color.text_primary)
    private val textSentMessageColor = ResourceUtils.getColor(com.project.core.R.color.text_white)

    fun bindWithTextMessage(
        isMine: Boolean,
        messagePosition: MessagePosition,
        content: String,
        largeRadius: Float = LARGE_RADIUS,
        smallRadius: Float = SMALL_RADIUS,
        backgroundReceivedMessageColor: Int? = null,
        backgroundSentMessageColor: Int? = null,
        textReceivedMessageColor: Int? = null,
        textSentMessageColor: Int? = null,
    ) {
        val receivedBackground =
            backgroundReceivedMessageColor ?: this.backgroundReceiveMessageColor
        val sentBackground = backgroundSentMessageColor ?: this.backgroundSentMessageColor
        val receivedTextColor = textReceivedMessageColor ?: this.textReceivedMessageColor
        val sentTextColor = textSentMessageColor ?: this.textSentMessageColor
        val backgroundColor = if (isMine) sentBackground else receivedBackground
        val textColor = if (isMine) sentTextColor else receivedTextColor
        setTextColor(textColor)
        text = content
        val radii = when (messagePosition) {
            MessagePosition.SINGLE -> floatArrayOf(
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius
            )

            MessagePosition.TOP -> if (isMine) floatArrayOf(
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                smallRadius,
                smallRadius,
                largeRadius,
                largeRadius
            )
            else floatArrayOf(
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                smallRadius,
                smallRadius
            )

            MessagePosition.MIDDLE -> if (isMine) floatArrayOf(
                largeRadius,
                largeRadius,
                smallRadius,
                smallRadius,
                smallRadius,
                smallRadius,
                largeRadius,
                largeRadius
            )
            else floatArrayOf(
                smallRadius,
                smallRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                smallRadius,
                smallRadius
            )

            MessagePosition.BOTTOM -> if (isMine) floatArrayOf(
                largeRadius,
                largeRadius,
                smallRadius,
                smallRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius
            )
            else floatArrayOf(
                smallRadius,
                smallRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius,
                largeRadius
            )
        }

        background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(backgroundColor)
            cornerRadii = radii
        }
    }
}