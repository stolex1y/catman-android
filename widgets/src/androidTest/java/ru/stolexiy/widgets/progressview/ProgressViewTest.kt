package ru.stolexiy.widgets.progressview

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.stolexiy.widgets.CustomMatchers.withButtonText
import ru.stolexiy.widgets.CustomMatchers.withPropertyValue
import ru.stolexiy.widgets.R
import ru.stolexiy.widgets.example.progressview.SimpleActivityWithProgressView

private const val RIGHT_HALF_SECTOR = 180f
private const val LEFT_HALF_SECTOR = -180f

@RunWith(AndroidJUnit4::class)
@MediumTest
class ProgressViewTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SimpleActivityWithProgressView::class.java)

    @Test
    fun progressViewShown() {
        onView(withId(R.id.progressView))
            .check(matches(withPropertyValue("0", ProgressView::text)))
    }

    @Test
    fun seFillingUpProgress_setHalfProgress__rightHalfCircle() {
        onView(withId(R.id.buttonFillingProgress))
            .perform(click())
            .check(matches(withButtonText(R.string.filling)))

        onView(withId(R.id.buttonClockwise))
            .check(matches(withButtonText(R.string.clockwise)))

        val halfProgressCount = SimpleActivityWithProgressView.MAX_COUNT / 2
        onView(withId(R.id.buttonCountUp)).run {
            (1..halfProgressCount).forEach { _ -> perform(click()) }
        }

        onView(withId(R.id.progressView))
            .check(matches(withPropertyValue(RIGHT_HALF_SECTOR, ProgressView::filledSector)))
    }

    @Test
    fun setDecreasingProgress_setHalfProgress__leftHalfCircle() {
        onView(withId(R.id.buttonFillingProgress))
            .check(matches(withButtonText(R.string.decreasing)))

        onView(withId(R.id.buttonClockwise))
            .check(matches(withButtonText(R.string.clockwise)))

        val halfProgressCount = SimpleActivityWithProgressView.MAX_COUNT / 2
        onView(withId(R.id.buttonCountUp)).run {
            (1..halfProgressCount).forEach { _ -> perform(click()) }
        }

        onView(withId(R.id.progressView))
            .check(matches(withPropertyValue(LEFT_HALF_SECTOR, ProgressView::filledSector)))
    }

    @Test
    fun setFillingUpProgress_setCounterclockwise_setHalfProgress__leftHalfCircle() {
        onView(withId(R.id.buttonFillingProgress))
            .perform(click())
            .check(matches(withButtonText(R.string.filling)))

        onView(withId(R.id.buttonClockwise))
            .perform(click())
            .check(matches(withButtonText(R.string.counterclockwise)))

        val halfProgressCount = SimpleActivityWithProgressView.MAX_COUNT / 2
        onView(withId(R.id.buttonCountUp)).run {
            (1..halfProgressCount).forEach { _ -> perform(click()) }
        }

        onView(withId(R.id.progressView))
            .check(matches(withPropertyValue(LEFT_HALF_SECTOR, ProgressView::filledSector)))
    }

    @Test
    fun setDecreasingProgress_setCounterclockwise_setHalfProgress__rightHalfCircle() {
        onView(withId(R.id.buttonFillingProgress))
            .check(matches(withButtonText(R.string.decreasing)))

        onView(withId(R.id.buttonClockwise))
            .perform(click())
            .check(matches(withButtonText(R.string.counterclockwise)))

        val halfProgressCount = SimpleActivityWithProgressView.MAX_COUNT / 2
        onView(withId(R.id.buttonCountUp)).run {
            (1..halfProgressCount).forEach { _ -> perform(click()) }
        }

        onView(withId(R.id.progressView))
            .check(matches(withPropertyValue(RIGHT_HALF_SECTOR, ProgressView::filledSector)))
    }
}