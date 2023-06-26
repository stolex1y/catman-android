package ru.stolexiy.common.timer

import kotlin.math.ceil

abstract class Time {
    abstract val inMs: Long
    abstract val min: Long
    abstract val sec: Long
    abstract val ms: Long

    fun secCeil(): Int {
        return ceil(this.sec.toFloat() + this.ms / 1000f).toInt()
    }

    operator fun compareTo(ms: Long): Int {
        return this.inMs.compareTo(ms)
    }

    operator fun compareTo(other: Time): Int {
        return this.inMs.compareTo(other.inMs)
    }

    override operator fun equals(other: Any?): Boolean {
        if (other == null)
            return false
        return when (other) {
            is Long -> this.inMs == other
            is Time -> this.inMs == other.inMs
            else -> false
        }
    }

    override fun hashCode(): Int {
        return inMs.hashCode()
    }

    companion object {
        val ZERO: Time = MutableTime(0)
    }
}
