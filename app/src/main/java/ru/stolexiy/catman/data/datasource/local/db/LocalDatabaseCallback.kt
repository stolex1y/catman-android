package ru.stolexiy.catman.data.datasource.local.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.stolexiy.catman.core.model.DefaultColors
import ru.stolexiy.catman.data.datasource.local.model.ColorEntity

class LocalDatabaseCallback : RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        DefaultColors.values().forEach { color ->
            val row = ContentValues().apply {
                put("color_int", color.rgba)
                put("color_name", color.nameRes)
            }
            db.insert(ColorEntity.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, row)
        }
    }
}
