package ru.stolexiy.common

import junit.framework.TestCase.assertEquals

object TimerTestUtil {
    fun Timer.verifyState(expectedState: Timer.State) {
        assertEquals("Timer state isn't valid", expectedState, this.state)
    }
}