package ru.stolexiy.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import ru.stolexiy.common.TimerTestUtil.pauseAndVerify
import ru.stolexiy.common.TimerTestUtil.resetAndVerify
import ru.stolexiy.common.TimerTestUtil.startAndVerify
import ru.stolexiy.common.TimerTestUtil.stopAndVerify
import ru.stolexiy.common.TimerTestUtil.verifyState
import ru.stolexiy.common.TimerTestUtil.verifyTimeToFinish

@OptIn(ExperimentalCoroutinesApi::class)
internal class TimerTest {
    companion object {
        private const val INIT_TIME_MS = 2 * 5 * TimeConstants.SEC_TO_MS
        private const val UPDATE_TIME_MS = 1 * TimeConstants.SEC_TO_MS
    }

    private val testScope = TestScope()
    private val testDispatcher = UnconfinedTestDispatcher(testScope.testScheduler)

    private val underTest =
        Timer(testDispatcher).apply {
            addListener(LogTimerListener)
            initTime = Timer.Time(INIT_TIME_MS)
            updateTime = Timer.Time(UPDATE_TIME_MS)
        }

    @Before
    fun verifyStopped() {
        underTest.verifyState(Timer.State.STOPPED)
    }

    @Test
    fun `timer is finished after init time`() = testScope.runTest {
        startAndVerify(underTest)
        verifyTimeToFinish(underTest, INIT_TIME_MS)
    }

    @Test
    fun `timer is not running after pause`() = testScope.runTest {
        startAndVerify(underTest)
        pauseAndVerify(underTest)

        val timeAfterPause = underTest.curTime.inMs
        advanceTimeBy(INIT_TIME_MS)
        assertEquals(timeAfterPause, underTest.curTime.inMs)
    }

    @Test
    fun `timer stop after call stop()`() = testScope.runTest {
        startAndVerify(underTest)
        stopAndVerify(underTest)

        val timeAfterStop = underTest.curTime.inMs
        advanceTimeBy(INIT_TIME_MS)
        assertEquals(timeAfterStop, underTest.curTime.inMs)
    }

    @Test
    fun `reset timer after start`() = testScope.runTest {
        startAndVerify(underTest)
        advanceTimeBy(INIT_TIME_MS / 2)
        assertNotEquals(INIT_TIME_MS, underTest.curTime.inMs)

        resetAndVerify(underTest)
        assertEquals(INIT_TIME_MS, underTest.curTime.inMs)
    }

    @Test
    fun `resume timer after pause`() = testScope.runTest {
        startAndVerify(underTest)

        val untilPause = INIT_TIME_MS / 2
        advanceTimeBy(untilPause)
        pauseAndVerify(underTest)

        startAndVerify(underTest)

        verifyTimeToFinish(underTest, INIT_TIME_MS - untilPause)
    }

    private object LogTimerListener : Timer.Listener {
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
