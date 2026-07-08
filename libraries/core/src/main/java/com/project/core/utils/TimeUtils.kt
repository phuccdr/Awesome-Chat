package com.project.core.utils

import com.google.firebase.Timestamp
import com.project.core.R
import com.project.core.utils.resource.ResourceUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TimeUtils {
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun format(timestamp: Timestamp?): String {
        if (timestamp == null) return ""
        val messageTime = timestamp.toDate()
        val now = Calendar.getInstance()
        val messageCal = Calendar.getInstance().apply {
            time = messageTime
        }

        return when {
            isSameDay(now, messageCal) -> {
                ResourceUtils.getString(R.string.today)
            }

            isYesterday(now, messageCal) -> {
                ResourceUtils.getString(R.string.yesterday)
            }

            else -> {
                dateFormat.format(messageTime)
            }
        }
    }

    private fun isSameDay(c1: Calendar, c2: Calendar): Boolean {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(
            Calendar.DAY_OF_YEAR
        )
    }

    private fun isYesterday(now: Calendar, message: Calendar): Boolean {
        val yesterday = Calendar.getInstance().apply {
            time = now.time
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return isSameDay(yesterday, message)
    }
}