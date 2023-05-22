package ru.stolexiy.catman.ui.dialog.purpose.add

import android.graphics.Color
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.WorkManager
import com.azimolabs.conditionwatcher.ConditionWatcher
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.stolexiy.catman.R
import ru.stolexiy.catman.domain.usecase.CategoryCrud
import ru.stolexiy.catman.domain.usecase.PurposeCrud
import ru.stolexiy.catman.ui.MainActivity
import ru.stolexiy.catman.ui.dialog.purpose.model.Category
import ru.stolexiy.catman.ui.dialog.purpose.model.Purpose
import ru.stolexiy.commontest.swipeToTop
import ru.stolexiy.commontest.waitAllWorkersInstruction
import ru.stolexiy.commontest.withButtonText
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AddPurposeDialogTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityScenarioRule = activityScenarioRule<MainActivity>()

    @Inject
    lateinit var categoryCrud: CategoryCrud

    @Inject
    lateinit var purposeCrud: PurposeCrud

    @Inject
    lateinit var workManager: WorkManager

    private var testCategory = Category(
        0,
        Color.RED,
        UUID.randomUUID().toString()
    )

    private val testPurpose by lazy {
        Purpose(
            UUID.randomUUID().toString(),
            testCategory,
            Calendar.getInstance()
        )
    }

    @Before
    fun openAddPurposeDialog() {
        onView(withId(R.id.add_purpose_button)).perform(click())
        onView(withId(R.id.purpose_name)).check(matches(isDisplayed()))
    }

    @Before
    fun fillDatabase() {
        hiltRule.inject()
        clearDatabase()
        runBlocking {
            categoryCrud.create(testCategory.toDomainCategory()).onSuccess {
                testCategory = testCategory.copy(id = it.first())
            }
        }
    }

    @Test
    fun addPurpose_purposeShownInList() {
        //pre-conditions
        assertThatNotContainsPurposeWithName(testPurpose.name.get())

        expandBottomSheet()

        fillPurposeDetailsWith(testPurpose)

        onView(withId(R.id.add_purpose_button))
            .perform(click())

        ConditionWatcher.waitForCondition(waitAllWorkersInstruction(workManager))

        //post-conditions
        assertThatContainsPurposeWithName(testPurpose.name.value)
        onView(withText(testPurpose.name.value)).check(matches(isDisplayed()))
    }

    private fun assertThatNotContainsPurposeWithName(purposeName: String) {
        runBlocking {
            purposeCrud.getAll().first().onSuccess { purposes ->
                assertThat(purposes.map { it.name }, not(containsInAnyOrder(purposeName)))
            }
        }
    }

    private fun assertThatContainsPurposeWithName(purposeName: String) {
        runBlocking {
            purposeCrud.getAll().first().onSuccess { purposes ->
                assertThat(purposes.map { it.name }, containsInAnyOrder(purposeName))
            }
        }
    }

    private fun expandBottomSheet() {
        onView(withId(R.id.view))
            .perform(swipeToTop())
    }

    private fun fillPurposeDetailsWith(purpose: Purpose) {
        onView(withId(R.id.purpose_name))
            .perform(typeText(purpose.name.get()))

        pressBack()

        onView(withId(R.id.purpose_category_layout))
            .perform(click())

        onView(withText(purpose.category.value!!.name))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())

        onView(withId(R.id.purpose_deadline))
            .perform(click())
        onView(withButtonText(R.string.choose))
            .perform(click())
    }

    @After
    fun clearDatabase() {
        runBlocking {
            purposeCrud.clear()
            categoryCrud.clear()
        }
    }

}