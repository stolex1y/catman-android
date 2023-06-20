package ru.stolexiy.common

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import ru.stolexiy.common.timer.Time
import ru.stolexiy.common.timer.Timer

@OptIn(ExperimentalCoroutinesApi::class)
internal object TimerTestUtil {
    private const val DELAY_SMALL = 10L

    fun Timer.verifyState(expectedState: Timer.State, msg: String = "Timer state isn't valid") {
        assertEquals(msg, expectedState, this.state)
    }

    fun TestScope.startAndVerify(timer: Timer) {
        timer.start()
        advanceTimeBy(DELAY_SMALL)
        timer.verifyState(Timer.State.RUNNING, "Timer must be RUNNING after call start()")
    }

    fun TestScope.pauseAndVerify(timer: Timer) {
        timer.pause()
        advanceTimeBy(DELAY_SMALL)
        timer.verifyState(Timer.State.PAUSED, "Timer must be PAUSED after call pause()")
    }

    fun TestScope.stopAndVerify(timer: Timer) {
        timer.stop()
        advanceTimeBy(DELAY_SMALL)
        timer.verifyState(Timer.State.STOPPED, "Timer must be PAUSED after call stop()")
    }

    fun TestScope.resetAndVerify(timer: Timer) {
        timer.reset()
        advanceTimeBy(DELAY_SMALL)
        timer.verifyState(Timer.State.STOPPED, "Timer must be PAUSED after call reset()")
    }

    fun TestScope.verifyTimeToFinish(timer: Timer, timeToFinish: Long) {
        require(timer.state == Timer.State.RUNNING) {
            "Timer must be RUNNING before verifying time to finish"
        }
        advanceTimeBy(timeToFinish)
        timer.verifyState(
            Timer.State.STOPPED,
            "Timer must be STOPPED after waiting time to finish: ${Time(timeToFinish)}, " +
                    "but actual is still time: ${timer.curTime}"
        )
    }
}
