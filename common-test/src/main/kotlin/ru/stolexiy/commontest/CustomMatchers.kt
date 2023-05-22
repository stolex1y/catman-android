package ru.stolexiy.commontest

import android.view.View
import android.widget.Button
import androidx.test.espresso.matcher.BoundedDiagnosingMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers

inline fun <reified V : View, T> withPropertyValue(
    value: T,
    crossinline selector: V.() -> T,
    propertyName: String = "property"
): Matcher<View> {
    return withPropertyValue(Matchers.`is`(value), selector, propertyName)
}

inline fun <reified V : View, T> withPropertyValue(
    matcher: Matcher<T>,
    crossinline selector: V.() -> T,
    propertyName: String = "property"
): Matcher<View> {
    return object : BoundedDiagnosingMatcher<View, V>(V::class.java) {
        override fun describeMoreTo(description: Description) {
            description.appendText("${V::class.simpleName ?: "view"}.$propertyName to match: ")
            matcher.describeTo(description)
        }

        override fun matchesSafely(item: V, mismatchDescription: Description): Boolean {
            return matcher.matches(selector(item))
        }
    }
}

fun withButtonText(expectedTextResId: Int): Matcher<View> {
    return object : BoundedDiagnosingMatcher<View, Button>(Button::class.java) {
        private var expectedText: String? = null

        override fun describeMoreTo(description: Description) {
            description.appendText(
                "Button.text is text from resources with id " +
                        "$expectedTextResId [${expectedText ?: ""}]"
            )
        }

        override fun matchesSafely(item: Button, mismatchDescription: Description): Boolean {
            if (expectedText == null)
                expectedText = item.resources.getString(expectedTextResId)
            return item.text == expectedText
        }
    }
}
