package ru.stolexiy.commontest

import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe

fun swipeToTop(): ViewAction {
    return GeneralSwipeAction(
        Swipe.FAST, GeneralLocation.CENTER,
        { view ->
            GeneralLocation.CENTER.calculateCoordinates(view).apply {
                this[1] = 0f
            }
        },
        Press.FINGER
    )
}
