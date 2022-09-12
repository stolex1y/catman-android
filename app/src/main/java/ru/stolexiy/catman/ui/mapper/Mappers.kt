package ru.stolexiy.catman.ui.mapper

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.toDmyString(): String =
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(this.time)

fun calendarFromMillis(millis: Long): Calendar = Calendar.getInstance().apply { timeInMillis = millis }