package ru.stolexiy.commontest

import org.junit.Assert
import kotlin.math.abs

object CustomAsserts {
    @JvmStatic
    fun assertEquals(expected: Long, actual: Long, delta: Long) =
        Assert.assertTrue(
            "Expected value: <$expected> differs from the actual: <$actual> " +
                    "by more than delta: <$delta>",
            abs(actual - expected) <= delta
        )
}
