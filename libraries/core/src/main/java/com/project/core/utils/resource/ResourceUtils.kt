package com.project.core.utils.resource

import android.app.Application
import android.graphics.Typeface
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

object ResourceUtils {

    lateinit var context: Application
        private set
    fun init(appContext: Application) {
        context = appContext
    }

    fun getString(@StringRes resId: Int): String =
        context.getString(resId)

    fun getString(@StringRes resId: Int, vararg args: Any): String =
        context.getString(resId, *args)

    fun getColor(@ColorRes resId: Int): Int =
        ContextCompat.getColor(context, resId)

    fun getDrawable(@DrawableRes resId: Int) =
        ContextCompat.getDrawable(context, resId)
    fun getFont(@FontRes fontId: Int): Typeface? =
        ResourcesCompat.getFont(context, fontId)
}