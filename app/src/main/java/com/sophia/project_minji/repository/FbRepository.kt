package com.sophia.project_minji.repository

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.dataclass.User
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.utillties.Constants
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext

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
        character: String
    ) {

        val imageUri = image.toString()
        val studentInFor = StudentEntity(name, birth, phNumber, imageUri, character)
        fireStore.collection(Constants.KEY_COLLECTION_STUDENT).document().set(studentInFor)
    }

    fun setStudentInFor(
        studentList: ArrayList<StudentEntity>,
        linearAdapter: StudentLinearAdapter,
        gridAdapter: StudentGridAdapter
    ) {
        fireStore.collection(Constants.KEY_COLLECTION_STUDENT).addSnapshotListener { value, _ ->
            if (value != null) {
                for (dc in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val stInFor = dc.document.toObject(StudentEntity::class.java)
                        stInFor.id = dc.document.id
                        stInFor.name = dc.document.get("name").toString()
                        stInFor.birth = dc.document.get("birth").toString()
                        stInFor.phNumber = dc.document.get("phNumber").toString()
                        stInFor.character = dc.document.get("character").toString()

                        studentList.add(stInFor)
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
                    val documentId = documentSnapshot.id
                    fireStore.collection("Student")
                        .document(documentId)
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