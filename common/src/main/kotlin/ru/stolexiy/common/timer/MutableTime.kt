package ru.stolexiy.common.timer

import kotlin.math.max

class MutableTime(ms: Long) : Time() {
    private var _inMs: Long = 0
    private var _h: Int = 0
    private var _min: Int = 0
    private var _sec: Int = 0
    private var _ms: Int = 0

    override val inMs: Long
        get() = _inMs
    override val h: Int
        get() = _h
    override val min: Int
        get() = _min
    override val sec: Int
        get() = _sec
    override val ms: Int
        get() = _ms

    init {
        setTime(ms)
    }

    constructor(h: Int = 0, min: Int = 0, sec: Int = 0, ms: Int = 0) : this(
        convertToMs(h, min, sec, ms)
    )

    constructor(time: Time) : this(time.inMs)

    operator fun minus(ms: Long) {
        setTime(max(this.inMs - ms, 0))
    }

    operator fun minus(time: Time) {
        setTime(max(this.inMs - time.inMs, 0))
    }

    fun setTime(inMs: Long) {
        this._inMs = inMs
        _h = (inMs / TimeConstants.H_TO_MS).toInt()
        _min = (inMs % TimeConstants.H_TO_MS / TimeConstants.MIN_TO_MS).toInt()
        _sec = (inMs % TimeConstants.MIN_TO_MS / TimeConstants.SEC_TO_MS).toInt()
        _ms = (inMs % TimeConstants.SEC_TO_MS).toInt()
    }

    fun setTime(time: Time) {
        setTime(time.inMs)
    }

    private companion object {
        @JvmStatic
        private fun convertToMs(h: Int = 0, min: Int = 0, sec: Int = 0, ms: Int = 0): Long {
            return (h * TimeConstants.H_TO_MS) +
                    (min * TimeConstants.MIN_TO_MS) +
                    (sec * TimeConstants.SEC_TO_MS) +
                    ms
        }
    }

    override fun toString(): String {
        return "$_min min, $_sec sec ($_inMs ms)"
    }
}
