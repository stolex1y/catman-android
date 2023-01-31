package ru.stolexiy.catman.data.datasource.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.stolexiy.catman.domain.model.DomainColor

@Entity(tableName = "colors")
data class ColorEntity(
    @PrimaryKey @ColumnInfo(name = "color") val color: Int,
    @ColumnInfo(name = "name") val name: String
) {
    fun toDomainColor() = DomainColor(color = color, name = name)
}

fun DomainColor.toColorEntity() = ColorEntity(
    color = color,
    name = name
)

fun Array<out DomainColor>.toColorEntities() =
    map(DomainColor::toColorEntity).toTypedArray()