package com.sophia.project_minji.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sophia.project_minji.repository.FbRepository
import java.lang.IllegalArgumentException

class FirebaseViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val fRepository = FbRepository(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseViewModel::class.java)) {
            return FirebaseViewModel(fRepository) as T
        }
        throw IllegalArgumentException("UnKnown class name.")
    }
}