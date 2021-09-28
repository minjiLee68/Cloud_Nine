package com.sophia.project_minji.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sophia.project_minji.repository.FbRepository
import com.sophia.project_minji.repository.TdRepository
import java.lang.IllegalArgumentException

class FbViewModelFactory(): ViewModelProvider.Factory {

    private val fRepository = FbRepository()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FbViewModel::class.java)) {
            return FbViewModel(fRepository) as T
        }
        throw IllegalArgumentException("UnKnown class name.")
    }
}