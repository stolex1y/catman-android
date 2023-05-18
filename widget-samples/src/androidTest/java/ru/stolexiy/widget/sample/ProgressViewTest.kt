package ru.stolexiy.widget.sample

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.stolexiy.widgets.ProgressView
import ru.stolexiy.widgets.sample.R
import ru.stolexiy.widgets.sample.progressview.SimpleActivityWithProgressView

private const val RIGHT_HALF_SECTOR = 180f
private const val LEFT_HALF_SECTOR = -180f

@RunWith(AndroidJUnit4::class)
@MediumTest
internal class ProgressViewTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SimpleActivityWithProgressView::class.java)

    @Test
    fun progressViewShown() {
        Espresso.onView(ViewMatchers.withId(R.id.progressView))
            .check(
                ViewAssertions.matches(
                    CustomMatchers.withPropertyValue(
                        "0",
                        ProgressView::text
                    )
                )
            )
    }

    @Test
    fun setFillingUpProgress_setHalfProgress__rightHalfCircle() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonFillingProgress))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(CustomMatchers.withButtonText(R.string.filling)))

        Espresso.onView(ViewMatchers.withId(R.id.buttonClockwise))
            .check(ViewAssertions.matches(CustomMatchers.withButtonText(R.string.clockwise)))

        val halfProgressCount = SimpleActivityWithProgressView.MAX_COUNT / 2
        Espresso.onView(ViewMatchers.withId(R.id.buttonCountUp)).run {
            (1..halfProgressCount).forEach { _ -> perform(ViewActions.click()) }
        }

        Espresso.onView(ViewMatchers.withId(R.id.progressView))
            .check(
                ViewAssertions.matches(
                    CustomMatchers.withPropertyValue(
                        RIGHT_HALF_SECTOR,
                        ProgressView::filledSector
                    )
                )
            )
    }

    @Test
    fun setDecreasingProgress_setHalfProgress__leftHalfCircle() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonFillingProgress))
            .check(ViewAssertions.matches(CustomMatchers.withButtonText(R.string.decreasing)))

        Espresso.onView(ViewMatchers.withId(R.id.buttonClockwise))
            .check(ViewAssertions.matches(CustomMatchers.withButtonText(R.string.clockwise)))

        val halfProgressCount = SimpleActivityWithProgressView.MAX_COUNT / 2
        Espresso.onView(ViewMatchers.withId(R.id.buttonCountUp)).run {
            (1..halfProgressCount).forEach { _ -> perform(ViewActions.click()) }
        }

        Espresso.onView(ViewMatchers.withId(R.id.progressView))
            .check(
                ViewAssertions.matches(
                    CustomMatchers.withPropertyValue(
                        LEFT_HALF_SECTOR,
                        ProgressView::filledSector
                    )
                )
            )
    }

    @Test
    fun setFillingUpProgress_setCounterclockwise_setHalfProgress__leftHalfCircle() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonFillingProgress))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(CustomMatchers.withButtonText(R.string.filling)))

        Espresso.onView(ViewMatchers.withId(R.id.buttonClockwise))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(CustomMatchers.withButtonText(R.string.counterclockwise)))

        val halfProgressCount = SimpleActivityWithProgressView.MAX_COUNT / 2
        Espresso.onView(ViewMatchers.withId(R.id.buttonCountUp)).run {
            (1..halfProgressCount).forEach { _ -> perform(ViewActions.click()) }
        }

        Espresso.onView(ViewMatchers.withId(R.id.progressView))
            .check(
                ViewAssertions.matches(
                    CustomMatchers.withPropertyValue(
                        LEFT_HALF_SECTOR,
                        ProgressView::filledSector
                    )
                )
            )
    }

    @Test
    fun setDecreasingProgress_setCounterclockwise_setHalfProgress__rightHalfCircle() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonFillingProgress))
            .check(ViewAssertions.matches(CustomMatchers.withButtonText(R.string.decreasing)))

        Espresso.onView(ViewMatchers.withId(R.id.buttonClockwise))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(CustomMatchers.withButtonText(R.string.counterclockwise)))

        val halfProgressCount = SimpleActivityWithProgressView.MAX_COUNT / 2
        Espresso.onView(ViewMatchers.withId(R.id.buttonCountUp)).run {
            (1..halfProgressCount).forEach { _ -> perform(ViewActions.click()) }
        }

        Espresso.onView(ViewMatchers.withId(R.id.progressView))
            .check(
                ViewAssertions.matches(
                    CustomMatchers.withPropertyValue(
                        RIGHT_HALF_SECTOR,
                        ProgressView::filledSector
                    )
                )
            )
    }
}
