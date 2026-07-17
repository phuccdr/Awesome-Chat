package com.project.core.utils

import com.project.core.utils.resource.ResourceUtils.context

fun Int.dpxoa123(): Int = (this * context.resources.displayMetrics.density).toInt()
fun Float.dpxoa123(): Float = (this * context.resources.displayMetrics.density)