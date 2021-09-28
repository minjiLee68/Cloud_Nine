package com.sophia.project_minji.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sophia.project_minji.entity.TodoEntity

@Database(entities = [TodoEntity::class], version = 1)
abstract class TodoDatabase: RoomDatabase() {

    abstract fun todoDao(): TodoDao

}