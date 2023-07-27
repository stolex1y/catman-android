package ru.stolexiy.common

import org.junit.Test
import ru.stolexiy.common.DateUtils.toEpochMillis
import java.time.ZonedDateTime

class DateUtilsTest {
    @Test
    fun `zoned date time to epoch millis`() {
        val expectedMillis: Long = System.currentTimeMillis()
        val dateTime = ZonedDateTime.now()
        CustomAsserts.assertEquals(expectedMillis, dateTime.toEpochMillis(), 100L)
    }
}
