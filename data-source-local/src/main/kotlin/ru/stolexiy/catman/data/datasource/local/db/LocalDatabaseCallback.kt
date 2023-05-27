package ru.stolexiy.catman.data.datasource.local.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.stolexiy.catman.data.datasource.local.model.ColorEntity
import ru.stolexiy.catman.data.datasource.local.model.DefaultColors

class LocalDatabaseCallback : RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        DefaultColors.values().forEach { color ->
            val row = ContentValues().apply {
                put("color_argb", color.argb)
                put("color_name", color.nameRes)
            }
            db.insert(ColorEntity.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, row)
        }
    }
}
