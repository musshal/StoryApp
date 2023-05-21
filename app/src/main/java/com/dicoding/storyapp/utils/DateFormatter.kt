package com.dicoding.storyapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    fun formatDate(date: String): String? {
        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        )
        val outputFormat = SimpleDateFormat("dd MMMM yyyy | HH:mm:ss", Locale.getDefault())

        val parsedDate: Date = inputFormat.parse(date) as Date

        return outputFormat.format(parsedDate)
    }
}