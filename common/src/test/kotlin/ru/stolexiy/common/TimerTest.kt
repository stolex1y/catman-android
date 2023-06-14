package ru.stolexiy.common

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
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

    @Test
    fun `timer is finished after init time without update listener`() = testScope.runTest {
        underTest.removeListener(LogTimerListener)
        startAndVerify(underTest)
        advanceUntilIdle()
        underTest.verifyState(Timer.State.STOPPED)
    }

    @Test
    fun `timer listener has been added`() = testScope.runTest {
        var added = false
        val listener = object : Timer.Listener {
            override fun onFinish(timer: Timer) {
                added = true
            }
        }
        underTest.addListener(listener)
        startAndVerify(underTest)
        advanceUntilIdle()
        assertTrue("Listener hasn't added", added)
    }

    @Test
    fun `timer listener has been removed`() = testScope.runTest {
        var added = false
        val listener = object : Timer.Listener {
            override fun onFinish(timer: Timer) {
                added = true
            }
        }
        underTest.addListener(listener)
        startAndVerify(underTest)
        underTest.removeListener(listener)
        advanceUntilIdle()
        underTest.verifyState(Timer.State.STOPPED)
        assertFalse("Listener hasn't removed", added)
    }

    @Test
    fun `on update called after updateTime in listener`() = testScope.runTest {
        val listener = createListenerVerifyingUpdateTime(UPDATE_TIME_MS)
        underTest.addListener(listener)
        startAndVerify(underTest)
        advanceUntilIdle()
    }

    @Test
    fun `on update called after updateTime in two different listeners`() = testScope.runTest {
        val firstListener = createListenerVerifyingUpdateTime(TimeConstants.SEC_TO_MS)
        val secondListener = createListenerVerifyingUpdateTime(60)
        underTest.addListener(firstListener)
        underTest.addListener(secondListener)
        startAndVerify(underTest)
        advanceUntilIdle()
    }

    private fun createListenerVerifyingUpdateTime(updateTime: Long): Timer.Listener {
        return object : Timer.Listener {
            var onUpdateTimeChecked = false
            var prevCurTime = underTest.initTime.inMs
            override val updateTime: Long
                get() = updateTime

            override fun onUpdateTime(timer: Timer) {
                onUpdateTimeChecked = true
                val actualUpdateTime = prevCurTime - timer.curTime.inMs
                prevCurTime = timer.curTime.inMs
                if (timer.curTime.inMs > 0L)
                    assertEquals(
                        "On update time called after the time different from the specified",
                        updateTime,
                        actualUpdateTime
                    )
            }

            override fun onFinish(timer: Timer) {
                assertTrue("onUpdateTime hasn't called", onUpdateTimeChecked)
            }
        }
    }

    private object LogTimerListener : Timer.Listener {
        override val updateTime: Long
            get() = UPDATE_TIME_MS

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
