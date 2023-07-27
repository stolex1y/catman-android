package ru.stolexiy.common.timer

import kotlin.math.ceil

abstract class Time {
    abstract val inMs: Long
    abstract val h: Int
    abstract val min: Int
    abstract val sec: Int
    abstract val ms: Int

    fun secCeil(): Int {
        return ceil(this.sec.toFloat() + this.ms / TimeConstants.SEC_TO_MS).toInt()
    }

    fun minCeil(): Int {
        return ceil(this.min.toFloat() + this.sec / TimeConstants.MIN_TO_SEC).toInt()
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

        @JvmStatic
        fun from(inMs: Long) = MutableTime(inMs)

        @JvmStatic
        fun from(h: Int = 0, min: Int = 0, sec: Int = 0, ms: Int = 0) = MutableTime(
            h, min, sec, ms
        )
    }
}
