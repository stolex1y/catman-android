package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.stolexiy.catman.domain.model.DomainWorkRegularity


@Entity(
    tableName = "work_regularity"
)
data class WorkRegularityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val type: DomainWorkRegularity.Type,
    val value: Int
)
