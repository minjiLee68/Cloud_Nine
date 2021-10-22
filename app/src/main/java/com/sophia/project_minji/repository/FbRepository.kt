package com.sophia.project_minji.repository

import android.annotation.SuppressLint
import android.net.Uri
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.utillties.Constants
import kotlin.collections.ArrayList

class FbRepository {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
                        val image = dc.document.toObject(StudentEntity::class.java)
                        image.id = dc.document.id

                        studentList.add(image)
                    }
                    linearAdapter.submitList(studentList)
                    gridAdapter.submitList(studentList)
                }
            }
        }
    }

    fun deleteStudent(position: Int) {
        fireStore.collection("Student").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val documentSnapshot: DocumentSnapshot = it.result!!.documents[position]
                val documentId = documentSnapshot.id
                fireStore.collection("Student")
                    .document(documentId)
                    .delete()
            }
        }
    }

}