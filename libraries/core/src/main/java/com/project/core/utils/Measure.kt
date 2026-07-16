package com.project.core.utils

import com.project.core.utils.resource.ResourceUtils.context

fun Int.dp(): Int = (this * context.resources.displayMetrics.density).toInt()
fun Float.dp(): Float = (this * context.resources.displayMetrics.density)