package ru.stolexiy.catman.ui.dialog.task

import ru.stolexiy.catman.ui.dialog.task.model.Task
import ru.stolexiy.catman.ui.util.udf.IEvent

sealed interface TaskSettingsDialogEvent : IEvent {
    object Load : TaskSettingsDialogEvent
    class Add(val task: Task) : TaskSettingsDialogEvent
    class Update(val task: Task) : TaskSettingsDialogEvent
    object DeleteAdded : TaskSettingsDialogEvent
    object RevertUpdate : TaskSettingsDialogEvent
    object Cancel : TaskSettingsDialogEvent
}
