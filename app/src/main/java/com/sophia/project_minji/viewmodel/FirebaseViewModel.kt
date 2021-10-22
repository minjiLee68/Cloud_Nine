package com.sophia.project_minji.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.repository.FbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(private val repository: FbRepository) : ViewModel() {

    fun register(
        name: String,
        birth: String,
        phNumber: String,
        image: Uri,
        character: String
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.register(name, birth, phNumber, image, character)
        }
    }

    fun setStudentInFor(
        studentList: ArrayList<StudentEntity>,
        linearAdapter: StudentLinearAdapter,
        gridAdapter: StudentGridAdapter
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.setStudentInFor(studentList, linearAdapter, gridAdapter)
        }

    }

    fun deleteStudent(position: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            repository.deleteStudent(position)
        }
    }

}