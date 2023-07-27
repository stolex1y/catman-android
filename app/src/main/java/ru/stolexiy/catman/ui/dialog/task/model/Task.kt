package ru.stolexiy.catman.ui.dialog.task.model

import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.catman.ui.dialog.common.model.Category
import ru.stolexiy.catman.ui.util.DefaultConditions
import ru.stolexiy.catman.ui.util.validation.ValidatedEntity
import ru.stolexiy.common.DateUtils.getDayLastMoment
import ru.stolexiy.common.DateUtils.toTomatoes
import ru.stolexiy.common.timer.Time
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class Task(
    name: String,
    startTime: ZonedDateTime?,
    deadline: ZonedDateTime?,
    description: String,
    isFinished: Boolean,
    timeCosts: Time?,
    timeCostsInTomatoes: Int?,
    purpose: Purpose?,
    var priority: Int,
    var id: Long
) : ValidatedEntity() {
    val name: ValidatedProperty<String> = validatedProperty(name, DefaultConditions.notEmpty())
    val startTime: ValidatedProperty<ZonedDateTime?> =
        validatedProperty(startTime, DefaultConditions.dateTimeFromToday())
    val deadline: ValidatedProperty<ZonedDateTime?> =
        validatedProperty(
            deadline,
            DefaultConditions.dateTimeFromToday() * DefaultConditions.notNull()
        )

    val isFinished: ValidatedProperty<Boolean> = validatedProperty(isFinished)

    val timeCosts: ValidatedProperty<Time?> = validatedProperty(timeCosts)

    val description: ValidatedProperty<String> = validatedProperty(description)

    val timeCostsInTomatoes: ValidatedProperty<Int?> = validatedProperty(
        timeCostsInTomatoes,
        DefaultConditions.positiveInt()
    )

    val purpose: ValidatedProperty<Purpose?> = validatedProperty(
        purpose,
        DefaultConditions.notNull()
    )

    val category: ValidatedProperty<Category?> = validatedProperty(
        purpose?.category,
        DefaultConditions.notNull()
    )

    constructor() : this(
        purpose = null,
        name = "",
        startTime = null,
        deadline = null,
        description = "",
        isFinished = false,
        priority = 0,
        timeCosts = null,
        id = 0,
        timeCostsInTomatoes = null,
    )

    fun toDomainTask() = DomainTask(
        purposeId = purpose.get()!!.id,
        name = name.get(),
        deadline = deadline.get() ?: purpose.get()!!.deadline.getDayLastMoment(),
        description = description.get(),
        isFinished = isFinished.get(),
        priority = priority,
        planTimeCosts = timeCosts.get(),
        id = id,
        startTime = startTime.get(),
        actualTimeCosts = Time.ZERO,
    )

    /*fun copy(other: DomainTask, purpose: Purpose) {
        name.set(other.name)
        deadline.set(other.deadline)
        description.set(other.description)
        isFinished.set(other.isFinished)
        timeCosts.set(other.planTimeCosts)
        startTime.set(other.startTime)
        this.purpose.set(purpose)
        priority = other.priority
        id = other.id
    }*/
}

fun DomainTask.toTask(purpose: Purpose) = Task(
    purpose = purpose,
    name = name,
    deadline = deadline,
    description = description,
    isFinished = isFinished,
    priority = priority,
    timeCosts = planTimeCosts,
    id = id,
    startTime = startTime,
    timeCostsInTomatoes = planTimeCosts?.toTomatoes()?.roundToInt()
)
