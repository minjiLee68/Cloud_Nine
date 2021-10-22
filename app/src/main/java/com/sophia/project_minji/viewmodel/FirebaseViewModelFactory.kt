package com.sophia.project_minji.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sophia.project_minji.repository.FbRepository
import java.lang.IllegalArgumentException

class FirebaseViewModelFactory: ViewModelProvider.Factory {

    private val fRepository = FbRepository()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseViewModel::class.java)) {
            return FirebaseViewModel(fRepository) as T
        }
        throw IllegalArgumentException("UnKnown class name.")
    }
}