package ru.stolexiy.catman.ui.mapper

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Calendar.toDmyString(): String =
    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(this.time)