package com.sophia.project_minji.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sophia.project_minji.repository.TodoRepository
import java.lang.IllegalArgumentException

class TodoViewModelFactory(application: Application): ViewModelProvider.Factory {

    private val tdRepository = TodoRepository(application)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            return TodoViewModel(tdRepository) as T
        }
        throw IllegalArgumentException("UnKnown class name.")
    }
}