package com.sophia.project_minji.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.sophia.project_minji.database.TodoDatabase
import com.sophia.project_minji.entity.TodoEntity

class TodoRepository(application: Application) {

    companion object {
        private const val DATABASE_NAME = "TodoDatabase"
    }

    private val db = Room.databaseBuilder(
        application, TodoDatabase::class.java, DATABASE_NAME
    ).build()

    private val todoDao = db.todoDao()

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