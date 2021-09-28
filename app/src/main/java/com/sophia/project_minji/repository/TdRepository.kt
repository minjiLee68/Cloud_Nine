package com.sophia.project_minji.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.sophia.project_minji.data.TodoDatabase
import com.sophia.project_minji.entity.TodoEntity

class TdRepository(application: Application) {

    companion object {
        private const val DATABASE_NAME = "TodoDatabase"
    }

    val db = Room.databaseBuilder(
        application, TodoDatabase::class.java, DATABASE_NAME
    ).build()

    val todoDao = db.todoDao()

    fun readDateData(year: Int, month: Int, day: Int): LiveData<List<TodoEntity>> =
        todoDao.readDateData(year, month, day)

    fun insert(todo: TodoEntity) {
        todoDao.insert(todo)
    }

    fun delete(todo: TodoEntity) {
        todoDao.delete(todo)
    }

    fun update(todo: TodoEntity) {
        todoDao.update(todo)
    }
}