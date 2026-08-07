package com.project.core.utils.textspan

import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.MetricAffectingSpan
import android.view.View

class CustomTypefaceSpan(
    private val typeface: Typeface
) : MetricAffectingSpan() {
    override fun updateDrawState(tp: TextPaint) {
        apply(tp)
    }

    override fun updateMeasureState(tp: TextPaint) {
        apply(tp)
    }

    private fun apply(paint: Paint) {
        paint.typeface = typeface
    }
}

fun SpannableString.setClickableSpan(
    start: Int, end: Int, color: Int, onClick: () -> Unit
) {
    setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            onClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = color
            ds.isUnderlineText = false
        }
    }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}