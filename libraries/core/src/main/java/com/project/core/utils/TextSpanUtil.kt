package com.project.core.utils

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View

object TextSpanUtil {

    data class SpanStyle(
        val color: Int? = null,
        val isBold: Boolean = false,
        val isItalic: Boolean = false,
        val isUnderline: Boolean = false,
        val textSize: Float? = null,
        val onClick: (() -> Unit)? = null
    )

    fun build(
        fullText: String,
        vararg spans: Pair<String, SpanStyle>
    ): SpannableStringBuilder {

        val builder = SpannableStringBuilder(fullText)
        spans.forEach { (target, style) ->

            var startIndex = fullText.indexOf(target)

            while (startIndex >= 0) {

                val endIndex = startIndex + target.length

                style.color?.let {
                    builder.setSpan(
                        ForegroundColorSpan(it),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                if (style.isBold) {
                    builder.setSpan(
                        StyleSpan(Typeface.BOLD),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                if (style.isItalic) {
                    builder.setSpan(
                        StyleSpan(Typeface.ITALIC),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                if (style.isUnderline) {
                    builder.setSpan(
                        UnderlineSpan(),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                style.textSize?.let {
                    builder.setSpan(
                        RelativeSizeSpan(it),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                style.onClick?.let { click ->
                    builder.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                click.invoke()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                ds.isUnderlineText = false
                            }
                        },
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                startIndex = fullText.indexOf(target, endIndex)
            }
        }

        return builder
    }
}