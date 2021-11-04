package com.sophia.project_minji.repository

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.utillties.Constants
import kotlin.collections.ArrayList

class FbRepository {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var _editLiveData = MutableLiveData<StudentEntity?>()
    val editLiveData: LiveData<StudentEntity?>
        get() = _editLiveData

    @SuppressLint("SimpleDateFormat")
    fun register(
        name: String,
        birth: String,
        phNumber: String,
        image: Uri,
        character: String,
        uid: String
    ) {

        val imageUri = image.toString()
        val studentInFor = StudentEntity(name, birth, phNumber, imageUri, character, uid)
        fireStore.collection(Constants.KEY_COLLECTION_STUDENT).document().set(studentInFor)
    }

    fun setStudentInFor(
        studentList: ArrayList<StudentEntity>,
        linearAdapter: StudentLinearAdapter,
        gridAdapter: StudentGridAdapter,
        id: String
    ) {
        val stInfor = StudentEntity()
//        fireStore.collection(Constants.KEY_COLLECTION_STUDENT).addSnapshotListener { value, _ ->
//            if (value != null) {
//                for (dc in value.documentChanges) {
//                    if (dc.type == DocumentChange.Type.ADDED) {
//                        val stInFor = dc.document.toObject(StudentEntity::class.java)
//                        stInFor.id = id
//                        stInFor.name = dc.document.get("name").toString()
//                        stInFor.birth = dc.document.get("birth").toString()
//                        stInFor.phNumber = dc.document.get("phNumber").toString()
//                        stInFor.character = dc.document.get("character").toString()
//
//                        studentList.add(stInFor)
//                    }
//                    linearAdapter.submitList(studentList)
//                    gridAdapter.submitList(studentList)
//                }
//            }
//        }
        fireStore.collection(Constants.KEY_COLLECTION_STUDENT)
            .whereEqualTo("id", id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (documentChange: DocumentChange in value.documentChanges) {
                        if (documentChange.type == DocumentChange.Type.ADDED) {
                            val studentEntity = StudentEntity()
                            studentEntity.id = documentChange.document.getString("id")
                            studentEntity.name = documentChange.document.getString("name")
                            studentEntity.birth = documentChange.document.getString("birth")
                            studentEntity.phNumber = documentChange.document.getString("phNumber")
                            studentEntity.character = documentChange.document.getString("character")
                            studentEntity.image = documentChange.document.getString("image")

                            studentList.add(studentEntity)
                        }
                        linearAdapter.submitList(studentList)
                        gridAdapter.submitList(studentList)
                    }
                }
            }
    }

    fun deleteStudent(position: Int) {
        fireStore.collection("Student")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot: DocumentSnapshot = task.result!!.documents[position]
                    fireStore.collection("Student")
                        .document(documentSnapshot.id)
                        .delete()
                }
            }
    }

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
        fireStore.collection("Student")
            .document(id)
            .update(map)
    }
}