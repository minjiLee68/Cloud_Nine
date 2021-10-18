package com.sophia.project_minji.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophia.project_minji.entity.TodoEntity
import com.sophia.project_minji.repository.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class TodoViewModel(private val repository: TodoRepository): ViewModel() {

    private var _currentData = MutableLiveData<TodoEntity>()
    val currentData: LiveData<TodoEntity>
    get() = _currentData

    fun insert(content: String, year: Int, month: Int, day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = TodoEntity(content,year, month, day)
            repository.insert(todo)
        }
    }

    fun update(content: String, year: Int, month: Int, day: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = currentData.value?.apply {
                this.content = content
                this.year = year
                this.month = month
                this.day = day
            } ?: throw Exception("")
            repository.update(todo)
        }
    }

    fun delete(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
           repository.delete(todo)
        }
    }

    fun readDateData(year: Int, month: Int, day: Int): LiveData<List<TodoEntity>> =
        repository.readDateData(year, month, day)
}