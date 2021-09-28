package com.sophia.project_minji.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sophia.project_minji.repository.TdRepository
import java.lang.IllegalArgumentException

class TdViewModelFactory(application: Application): ViewModelProvider.Factory {

    private val tdRepository = TdRepository(application)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TdViewModel::class.java)) {
            return TdViewModel(tdRepository) as T
        }
        throw IllegalArgumentException("UnKnown class name.")
    }
}