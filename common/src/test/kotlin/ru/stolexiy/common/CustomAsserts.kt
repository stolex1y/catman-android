package ru.stolexiy.common

import org.junit.Assert
import kotlin.math.abs

internal object CustomAsserts {
    fun assertEquals(expected: Long, actual: Long, delta: Long) =
        Assert.assertTrue(
            "Expected value: <$expected> differs from the actual: <$actual> " +
                    "by more than delta: <$delta>",
            abs(actual - expected) <= delta
        )
}