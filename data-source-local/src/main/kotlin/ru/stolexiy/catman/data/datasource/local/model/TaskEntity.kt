package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.stolexiy.catman.domain.model.DomainTask
import ru.stolexiy.common.timer.Time
import java.time.ZonedDateTime

@Entity(
    tableName = Tables.Tasks.NAME,
    foreignKeys = [
        ForeignKey(
            entity = PurposeEntity::class,
            parentColumns = [Tables.Purposes.Fields.ID],
            childColumns = [Tables.Tasks.Fields.PURPOSE_ID],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(Tables.Tasks.Fields.PURPOSE_ID), Index(
        Tables.Tasks.Fields.PURPOSE_ID,
        Tables.Tasks.Fields.PRIORITY
    )]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = Tables.Tasks.Fields.ID) val id: Long,
    @ColumnInfo(name = Tables.Tasks.Fields.PURPOSE_ID) val purposeId: Long,
    @ColumnInfo(name = Tables.Tasks.Fields.NAME) val name: String,
    @ColumnInfo(name = Tables.Tasks.Fields.DESCRIPTION) val description: String,
    @ColumnInfo(name = Tables.Tasks.Fields.DEADLINE) val deadline: ZonedDateTime,
    @ColumnInfo(name = Tables.Tasks.Fields.PRIORITY) val priority: Int,
    @ColumnInfo(name = Tables.Tasks.Fields.IS_FINISHED) val isFinished: Boolean,
    @ColumnInfo(name = Tables.Tasks.Fields.PLAN_TIME_COSTS) val planTimeCosts: Time?,
    @ColumnInfo(name = Tables.Tasks.Fields.START_TIME) val startTime: ZonedDateTime?,
    @ColumnInfo(name = Tables.Tasks.Fields.COMPLETION_TIME) val completionTime: ZonedDateTime?,
    @ColumnInfo(name = Tables.Tasks.Fields.ACTUAL_TIME_COSTS) val actualTimeCosts: Time,
) {


    fun toDomainTask() = DomainTask(
        purposeId = purposeId,
        name = name,
        deadline = deadline,
        description = description,
        isFinished = isFinished,
        priority = priority,
        id = id,
        planTimeCosts = planTimeCosts,
        startTime = startTime,
        completionTime = completionTime,
        actualTimeCosts = actualTimeCosts,
    )
}

fun DomainTask.toTaskEntity() = TaskEntity(
    id = id,
    name = name,
    purposeId = purposeId,
    deadline = deadline,
    description = description,
    priority = priority,
    isFinished = isFinished,
    planTimeCosts = planTimeCosts,
    startTime = startTime,
    completionTime = completionTime,
    actualTimeCosts = actualTimeCosts,
)
