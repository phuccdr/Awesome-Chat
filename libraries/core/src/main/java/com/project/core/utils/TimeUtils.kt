package com.project.core.utils

import com.google.firebase.Timestamp
import com.project.core.R
import com.project.core.utils.resource.ResourceUtils
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

fun Timestamp.toMillis(): Long {
    return seconds * 1000 + nanoseconds / 1_000_000
}

fun Timestamp?.format(): String {
    if (this == null) return ""
    val messageTime = this.toDate()
    val now = Calendar.getInstance()
    val messageCal = Calendar.getInstance().apply {
        time = messageTime
    }

    return when {
        now.isSameDay(messageCal) -> {
            timeFormat.format(messageTime)
        }

        isYesterday(now, messageCal) -> {
            "${ResourceUtils.getString(R.string.yesterday)}: ${timeFormat.format(messageTime)}"
        }

        else -> {
            dateFormat.format(messageTime)
        }
    }
}

fun Calendar.isSameDay(c: Calendar): Boolean {
    return this.get(Calendar.YEAR) == c.get(Calendar.YEAR) && this.get(Calendar.DAY_OF_YEAR) == c.get(
        Calendar.DAY_OF_YEAR
    )
}

fun Timestamp.isSameDay(other: Timestamp): Boolean {
    val zone = ZoneId.systemDefault()
    val thisDate: LocalDate =
        Instant.ofEpochSecond(seconds, nanoseconds.toLong()).atZone(zone).toLocalDate()
    val otherDate: LocalDate =
        Instant.ofEpochSecond(other.seconds, other.nanoseconds.toLong()).atZone(zone).toLocalDate()

    return thisDate == otherDate
}

private fun isYesterday(now: Calendar, message: Calendar): Boolean {
    val yesterday = Calendar.getInstance().apply {
        time = now.time
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return yesterday.isSameDay(message)
}

fun Timestamp.toLocalDate(): LocalDate {
    return Instant.ofEpochSecond(seconds, nanoseconds.toLong()).atZone(ZoneId.systemDefault())
        .toLocalDate()
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun LocalDate.toDisplayText(): String {
    val today = LocalDate.now()
    return when (this) {
        today -> ResourceUtils.getString(R.string.today)
        today.minusDays(1) -> ResourceUtils.getString(R.string.yesterday)
        else -> format(dateFormatter)
    }
}
