package ru.stolexiy.common.timer

object TimeConstants {
    const val H_TO_MIN: Int = 60
    const val MIN_TO_SEC: Int = 60
    const val SEC_TO_MS: Long = 1000L
    const val MIN_TO_MS: Long = MIN_TO_SEC * SEC_TO_MS
    const val H_TO_MS: Long = H_TO_MIN * MIN_TO_MS
}
