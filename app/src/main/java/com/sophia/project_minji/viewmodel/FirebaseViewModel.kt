package com.sophia.project_minji.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.listeners.CallAnotherActivityNavigator
import com.sophia.project_minji.repository.FbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(private val repository: FbRepository) : ViewModel() {

    fun setUser(name: String, email: String, navigator: CallAnotherActivityNavigator) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setUser(name, email, navigator)
        }
    }

    fun register(
        name: String,
        birth: String,
        phNumber: String,
        image: Uri,
        character: String,
        navigator: CallAnotherActivityNavigator
    ) {
        repository.register(name, birth, phNumber, image, character, navigator)
    }

//    fun setStudentInFor(
//        studentList: MutableList<StudentEntity>,
//        linearAdapter: StudentLinearAdapter,
//        gridAdapter: StudentGridAdapter
//    ) {
//        repository.setStudentInFor(studentList,linearAdapter, gridAdapter)
//    }

    fun setStudentInFor(studentList: MutableList<StudentEntity>): LiveData<List<StudentEntity>> {
        repository.setStudentInFor(studentList)
        return repository.getStudentLive()
    }

    fun deleteStudent(position: Int) {
        repository.deleteStudent(position)
    }

    fun updateStudent(
        id: String,
        name: String,
        birth: String,
        phNumber: String,
        character: String
    ) {
        repository.updateStudent(id, name, birth, phNumber, character)
    }

}