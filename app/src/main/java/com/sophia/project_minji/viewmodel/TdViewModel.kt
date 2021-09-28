package com.sophia.project_minji.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophia.project_minji.entity.TodoEntity
import com.sophia.project_minji.repository.TdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class TdViewModel(private val repository: TdRepository): ViewModel() {

    private var _currentData = MutableLiveData<List<TodoEntity>>()
    val currentData: LiveData<List<TodoEntity>>
    get() = _currentData

//    fun saveTodo(todo: TodoEntity) {
//        viewModelScope.launch {
//            if (hashCurrentTodo()) {
//                update(todo)
//            } else {
//                insert(todo)
//            }
//        }
//    }

    private fun hashCurrentTodo() = _currentData.value != null

    fun insert(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(todo)
        }
    }

    fun update(todo: TodoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
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