package ru.stolexiy.catman.data.datasource.local.model

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.stolexiy.catman.domain.model.DomainColor

@Entity(tableName = ColorEntity.TABLE_NAME)
data class ColorEntity(
    @PrimaryKey @ColumnInfo(name = "color_int") val color: Int,
    @ColumnInfo(name = "color_name") @StringRes val name: Int
) {
    fun toDomainColor() = DomainColor(color = color, name = name)

    companion object {
        internal const val TABLE_NAME = "colors"
    }
}

fun DomainColor.toColorEntity() = ColorEntity(
    color = color,
    name = name
)

fun Array<out DomainColor>.toColorEntities() =
    map(DomainColor::toColorEntity).toTypedArray()