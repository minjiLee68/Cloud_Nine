package com.sophia.project_minji.viewmodel

import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.*
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.entity.Chat
import com.sophia.project_minji.entity.FollowUser
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.entity.User
import com.sophia.project_minji.listeners.CallAnotherActivityNavigator
import com.sophia.project_minji.repository.FbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseViewModel(private val repository: FbRepository) : ViewModel() {

    fun setUser(name: String, image: String, navigator: CallAnotherActivityNavigator) {
        repository.setUser(name, image, navigator)
    }

    fun userProfile(
        name: String,
        isPhotoSelected: Boolean,
        mImageUri: Uri,
        navigator: CallAnotherActivityNavigator
    ) {
        repository.userProfile(name, isPhotoSelected, mImageUri, navigator)
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

    fun getUsers(userList: MutableList<User>): LiveData<List<User>> {
        repository.getUsers(userList)
        return repository.getUserLive()
    }

    fun sendMessage(message: String, time: String, userId: String) {
        repository.sendMessage(message, time, userId)
    }

    fun getMessage(userId: String, chatMessages: MutableList<Chat>): LiveData<List<Chat>> {
        repository.getMessageId(userId, chatMessages)
        return repository.getChatLive()
    }

    fun getFollowUsers(userList: MutableList<FollowUser>): LiveData<List<FollowUser>> {
        repository.getFollowUser(userList)
        return repository.getFollowUserLive()
    }

    fun setFollowUser(userId: String) {
        repository.setFollowUser(userId)
    }

}