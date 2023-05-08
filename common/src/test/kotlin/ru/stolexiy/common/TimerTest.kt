package ru.stolexiy.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.stolexiy.common.CustomAsserts.assertEquals
import ru.stolexiy.common.TimerTestUtil.verifyState

@OptIn(ExperimentalCoroutinesApi::class)
internal class TimerTest {
    companion object {
        private const val INIT_TIME_MS = 2 * 5 * TimeConstants.SEC_TO_MS
        private const val UPDATE_TIME_MS = 1 * TimeConstants.SEC_TO_MS
        private const val DELAY_SMALL = 10L
    }

    private val testScope = TestScope()
    private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)

    private val underTest = Timer(
        testDispatcher,
        Timer.Time(INIT_TIME_MS),
        Timer.Time(UPDATE_TIME_MS)
    ).apply {
        listener = LogTimerListener
    }

    @Test
    fun `timer is finished after INIT_TIME_MS time`() = testScope.runTest {
        underTest.apply {
            verifyState(Timer.State.STOPPED)
            start()
            advanceTimeBy(DELAY_SMALL)
            verifyState(Timer.State.RUNNING)
            advanceUntilIdle()
            verifyState(Timer.State.STOPPED)
            assertEquals(currentTime, INIT_TIME_MS)
        }
    }

    @Test
    fun `timer is not running after pause`() = testScope.runTest {
        underTest.apply {
            verifyState(Timer.State.STOPPED)
            start()
            advanceTimeBy(DELAY_SMALL)
            verifyState(Timer.State.RUNNING)
            pause()
            advanceTimeBy(DELAY_SMALL)
            verifyState(Timer.State.PAUSED)
            val timeAfterPause = curTime.inMs
            advanceTimeBy(INIT_TIME_MS)
            assertEquals(timeAfterPause, curTime.inMs)
        }
    }

    @Test
    fun `timer stop after call stop()`() = testScope.runTest {
        underTest.apply {
            start()
            advanceTimeBy(DELAY_SMALL)
            stop()
            advanceTimeBy(DELAY_SMALL)
            verifyState(Timer.State.STOPPED)
            val timeAfterPause = curTime.inMs
            advanceTimeBy(INIT_TIME_MS)
            assertEquals(timeAfterPause, curTime.inMs)
        }
    }

    @Test
    fun `reset timer after start`() = testScope.runTest {
        underTest.apply {
            start()
            advanceTimeBy(DELAY_SMALL)
            advanceTimeBy(INIT_TIME_MS / 2)
            assertEquals(INIT_TIME_MS / 2, curTime.inMs, DELAY_SMALL)
            reset()
            advanceTimeBy(DELAY_SMALL)
            verifyState(Timer.State.STOPPED)
            assertEquals(INIT_TIME_MS, curTime.inMs)
        }
    }

    private object LogTimerListener : Timer.TimerListener {
        override fun onStart(timer: Timer) {
            println("timer started at ${System.currentTimeMillis()}")
        }

        override fun onStop(timer: Timer) {
            println("timer stopped at ${System.currentTimeMillis()}")
        }

        override fun onPause(timer: Timer) {
            println("timer paused at ${System.currentTimeMillis()}")
        }

        override fun onFinish(timer: Timer) {
            println("timer finished at ${System.currentTimeMillis()}")
        }
    }
}
