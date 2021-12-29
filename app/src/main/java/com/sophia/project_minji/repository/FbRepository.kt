package com.sophia.project_minji.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sophia.project_minji.dataclass.Chat
import com.sophia.project_minji.dataclass.FollowUser
import com.sophia.project_minji.dataclass.StudentEntity
import com.sophia.project_minji.dataclass.User
import com.sophia.project_minji.entity.*
import com.sophia.project_minji.listeners.CallAnotherActivityNavigator

class FbRepository(context: Context) {

    private lateinit var downloadUri: Uri
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var Uid: String = auth.currentUser?.uid.toString()
    private var progressBar: ProgressBar = ProgressBar(context)

    private val _studentLive = MutableLiveData<List<StudentEntity>>()
    fun getStudentLive(): LiveData<List<StudentEntity>> = _studentLive

    private val _userLive = MutableLiveData<List<User>>()
    fun getUserLive(): LiveData<List<User>> = _userLive

    private val _followUserLive = MutableLiveData<List<FollowUser>>()
    fun getFollowUserLive(): LiveData<List<FollowUser>> = _followUserLive

    private val _chatLive = MutableLiveData<List<Chat>>()
    fun getChatLive(): LiveData<List<Chat>> = _chatLive

    //사용자 프로필 만들기
    private fun setUser(
        name: String,
        image: String,
        school: String,
        introduce: String,
        webSite: String,
        navigator: CallAnotherActivityNavigator
    ) {
        val user = User(name, image, school, introduce, webSite)
        firestore.collection("Users").document(Uid).set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("tag", Uid)
                    navigator.callActivity()
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
    }

    //학생정보 데이터베이스에 저장
    @SuppressLint("SimpleDateFormat")
    fun register(
        name: String,
        birth: String,
        phNumber: String,
        image: Uri,
        character: String,
        navigator: CallAnotherActivityNavigator
    ) {
        val inforRef = storageReference.child("student_images")
            .child("${FieldValue.serverTimestamp()}.jpg")
        inforRef.putFile(image).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressBar.visibility = View.INVISIBLE
                inforRef.downloadUrl.addOnSuccessListener { uri ->
                    val student =
                        StudentEntity(name, birth, phNumber, uri.toString(), character, Uid)
                    firestore.collection("Students").document().set(student)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navigator.callActivity()
                                progressBar.visibility = View.INVISIBLE
                            }
                        }
                }
            }
        }
    }

    fun userProfile(
        name: String,
        school: String,
        introduce: String,
        webSite: String,
        isPhotoSelected: Boolean,
        mImageUri: Uri,
        navigator: CallAnotherActivityNavigator
    ) {
        val imageRef = storageReference.child("Profile_pics").child("$Uid.jpg")
        if (isPhotoSelected) {
            if (name.isNotEmpty()) {
                storageReference.child("Profile").child("$name.jpg")
                imageRef.putFile(mImageUri).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            downloadUri = uri
                            setUser(name, downloadUri.toString(), school, introduce, webSite,navigator)
                        }
                    }
                }
            }
        } else {
            downloadUri = mImageUri
            setUser(name,downloadUri.toString(),school, introduce, webSite,navigator)
        }
    }

    fun setStudentInFor(studentList: MutableList<StudentEntity>): LiveData<List<StudentEntity>> {
        firestore.collection("Students")
            .whereEqualTo("user", Uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (documentChange: DocumentChange in value.documentChanges) {
                        if (documentChange.type == DocumentChange.Type.ADDED) {
                            val userId = documentChange.document.id
                            val studentEntity =
                                documentChange.document.toObject(StudentEntity::class.java)
                                    .withId(userId)
                            studentEntity.name = documentChange.document.getString("name")!!
                            studentEntity.birth = documentChange.document.getString("birth")!!
                            studentEntity.phNumber = documentChange.document.getString("phNumber")!!
                            studentEntity.character =
                                documentChange.document.getString("character")!!
                            studentEntity.image = documentChange.document.getString("image")!!
                            studentEntity.user = documentChange.document.getString("user")!!

                            studentList.add(studentEntity)

                            _studentLive.value = studentList
                        }
                    }
                }
            }
        return _studentLive
    }

    //학생정보 삭제
    fun deleteStudent(position: Int) {
        firestore.collection("Students").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot: DocumentSnapshot = task.result!!.documents[position]
                firestore.collection("Students")
                    .document(documentSnapshot.id)
                    .delete()
            }
        }
    }

    //학생 정보 업데이트
    fun updateStudent(
        id: String,
        name: String,
        birth: String,
        phNumber: String,
        character: String
    ) {
        val map = mutableMapOf<String, Any>()
        map["name"] = name
        map["birth"] = birth
        map["phNumber"] = phNumber
        map["character"] = character
        firestore.collection("Students")
            .document(id)
            .update(map)
    }

    //어플 사용자 가져오기
    fun getUsers(userList: MutableList<User>): LiveData<List<User>> {
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection("Users").addSnapshotListener { value, _ ->
            if (value != null) {
                for (dc: DocumentChange in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        if (Uid == dc.document.id) {
                            continue
                        }
                        val userId = dc.document.id
                        val user = dc.document.toObject(User::class.java)
                        user.name = dc.document.getString("name")!!
                        user.image = dc.document.getString("image")!!
                        user.userId = userId

                        userList.add(user)
                        _userLive.value = userList
                    }
                }
            }
        }
        return _userLive
    }

    fun setFollowingUser(userId: String) {
        firestore.collection("Users").document(userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val name = task.result!!.getString("name")!!
                        val image = task.result!!.getString("image")!!
                        val followUser = FollowUser(name, image, userId)
                        firestore.collection("Users/$Uid/follow").document(userId).set(followUser)
                    }
                }
            }
        firestore.collection("Users").document(Uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val name = task.result!!.getString("name")!!
                        val image = task.result!!.getString("image")!!
                        val followUser = FollowUser(name, image, Uid)
                        firestore.collection("Users/$userId/follow").document(Uid).set(followUser)
                    }
                }
            }
    }

    fun getFollowUser(userList: MutableList<FollowUser>): LiveData<List<FollowUser>> {
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection("Users/$Uid/follow")
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            if (Uid == dc.document.id) {
                                continue
                            }
                            val user = dc.document.toObject(FollowUser::class.java)
                            val userId = dc.document.id
                            user.name = dc.document.getString("name")!!
                            user.image = dc.document.getString("image")!!
                            user.userId = userId

                            userList.add(user)
                            _followUserLive.value = userList
                        }
                    }
                }
            }
        return _followUserLive
    }

    fun deleteFollowUser(userId: String) {
        firestore.collection("Users/$Uid/follow").document(userId).delete()
    }

    fun sendMessage(message: String, time: String, userId: String) {
        firestore.collection("Users").document(Uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val name = task.result!!.getString("name")!!
                        val image = task.result!!.getString("image")!!
                        val chats = Chat(message, time, Uid, userId, image)
                        firestore.collection("Users/$userId/Chats").document().set(chats)
                    }
                }
            }
    }

    fun getMessageId(
        userId: String,
        chatMessages: MutableList<Chat>,
    ): LiveData<List<Chat>> {
        firestore.collection("Users/$userId/Chats")
            .whereEqualTo("sendId", Uid)
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val chats = dc.document.toObject(Chat::class.java)
                            chats.message = dc.document.getString("message")!!
                            chats.sendId = dc.document.getString("sendId")!!
                            chats.receiverId = dc.document.getString("receiverId")!!
                            chats.userProfile = dc.document.getString("userProfile")!!
                            chats.time = dc.document.getString("time")!!

                            chatMessages.add(chats)
                            _chatLive.value = chatMessages
                        }
                    }
                }
            }

        firestore.collection("Users/$Uid/Chats")
            .whereEqualTo("sendId", userId)
            .whereEqualTo("receiverId", Uid)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            val chats = dc.document.toObject(Chat::class.java)
                            chats.message = dc.document.getString("message")!!
                            chats.sendId = dc.document.getString("sendId")!!
                            chats.receiverId = dc.document.getString("receiverId")!!
                            chats.userProfile = dc.document.getString("userProfile")!!
                            chats.time = dc.document.getString("time")!!

                            chatMessages.add(chats)
                            _chatLive.value = chatMessages
                        }
                    }
                }
            }

        return _chatLive
    }
}