package ru.stolexiy.common.timer

import kotlin.math.max

class Time(ms: Long) : ImmutableTime() {
    private var _inMs: Long = 0
    private var _min: Long = 0
    private var _sec: Long = 0
    private var _ms: Long = 0

    override val inMs: Long
        get() = _inMs
    override val min: Long
        get() = _min
    override val sec: Long
        get() = _sec
    override val ms: Long
        get() = _ms

    init {
        setTime(ms)
    }

    constructor(min: Long, sec: Long) : this(minAndSecToMs(min, sec))

    constructor(time: ImmutableTime) : this(time.inMs)

    operator fun minus(ms: Long) {
        setTime(max(this.inMs - ms, 0))
    }

    operator fun minus(time: ImmutableTime) {
        setTime(max(this.inMs - time.inMs, 0))
    }

    fun setTime(inMs: Long) {
        this._inMs = inMs
        _min = inMs / TimeConstants.MIN_TO_MS
        _sec = inMs % TimeConstants.MIN_TO_MS / TimeConstants.SEC_TO_MS
        _ms = inMs % TimeConstants.SEC_TO_MS
    }

    fun setTime(time: ImmutableTime) {
        setTime(time.inMs)
    }

    private companion object {
        @JvmStatic
        private fun minAndSecToMs(min: Long, sec: Long): Long {
            return (min * TimeConstants.MIN_TO_SEC + sec) * TimeConstants.SEC_TO_MS
        }
    }

    override fun toString(): String {
        return "$_min min, $_sec sec ($_inMs ms)"
    }
}
