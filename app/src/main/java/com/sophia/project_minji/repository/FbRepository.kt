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
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.entity.User
import com.sophia.project_minji.listeners.CallAnotherActivityNavigator
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import kotlin.collections.ArrayList

class FbRepository(context: Context) {

    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var Uid: String = auth.currentUser?.uid.toString()
    private var progressBar: ProgressBar = ProgressBar(context)

    private val _studentLive = MutableLiveData<List<StudentEntity>>()
    fun getStudentLive(): LiveData<List<StudentEntity>> = _studentLive

    //사용자 프로필 만들기
    fun setUser(name: String, image: String, navigator: CallAnotherActivityNavigator) {
        val user = User(name, image)
        firestore.collection("Users").document(Uid).set(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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

    //학생정보 가져오기
//    fun setStudentInFor(
//        studentList: MutableList<StudentEntity>,
//        linearAdapter: StudentLinearAdapter,
//        gridAdapter: StudentGridAdapter
//    ) {
//        firestore.collection("Students")
//            .whereEqualTo("user", Uid)
//            .addSnapshotListener { value, error ->
//                if (error != null) {
//                    return@addSnapshotListener
//                }
//                if (value != null) {
//                    for (documentChange: DocumentChange in value.documentChanges) {
//                        if (documentChange.type == DocumentChange.Type.ADDED) {
//                            val userId = documentChange.document.id
//                            val studentEntity = documentChange.document.toObject(StudentEntity::class.java).withId(userId)
//                            studentEntity.name = documentChange.document.getString("name")!!
//                            studentEntity.birth = documentChange.document.getString("birth")!!
//                            studentEntity.phNumber = documentChange.document.getString("phNumber")!!
//                            studentEntity.character = documentChange.document.getString("character")!!
//                            studentEntity.image = documentChange.document.getString("image")!!
//                            studentEntity.user = documentChange.document.getString("user")!!
//
//                            studentList.add(studentEntity)
//
//                            linearAdapter.submitList(studentList)
//                            gridAdapter.submitList(studentList)
//                        }
//                    }
//                }
//            }
//    }

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
                            val studentEntity = documentChange.document.toObject(StudentEntity::class.java).withId(userId)
                            studentEntity.name = documentChange.document.getString("name")!!
                            studentEntity.birth = documentChange.document.getString("birth")!!
                            studentEntity.phNumber = documentChange.document.getString("phNumber")!!
                            studentEntity.character = documentChange.document.getString("character")!!
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
                firestore.collection("Student")
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
}