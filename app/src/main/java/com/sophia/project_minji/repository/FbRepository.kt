package com.sophia.project_minji.repository

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FbRepository(context: Context) {

    //    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var preferenceManager: PreferenceManager = PreferenceManager(context)

    //firebase
    @SuppressLint("SimpleDateFormat")
    fun register(
        students: HashMap<String, Any>,
        name: String,
        birth: String,
        phNumber: String,
        image: Uri,
        character: String
    ) {
//        students[Constants.KEY_STUDENT_NAME] = name
//        students[Constants.KEY_STUDENT_BIRTH] = birth
//        students[Constants.KEY_STUDENT_PHNUMBER] = phNumber
//        students[Constants.KEY_IMAGE] = image
//        students[Constants.KEY_STUDENT_CHARACTER] = character
//        fireStore.collection("Student")
//            .add(students)
//            .addOnSuccessListener { documentReference ->
//                preferenceManager.putString(Constants.KEY_STUDENT_ID, documentReference.id)
//                preferenceManager.putString(Constants.KEY_STUDENT_NAME, name)
//                preferenceManager.putString(Constants.KEY_STUDENT_BIRTH, birth)
//                preferenceManager.putString(Constants.KEY_STUDENT_PHNUMBER, phNumber)
//                preferenceManager.putString(Constants.KEY_STUDENT_IMAGE, image)
//                preferenceManager.putString(Constants.KEY_STUDENT_CHARACTER, character)
//            }
//        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
//        storage.reference.child("Student").child(fileName)
//            .putFile(image)
//            .addOnSuccessListener { taskSnapshot ->

//                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
//                    val imageUri = it.toString()
        val images = image.toString()
        val student = StudentEntity(name, birth, phNumber, images, character)
        fireStore.collection("Student")
            .document().set(student)
//                }
//            }
    }

    fun setFirestore(
        studentList: ArrayList<StudentEntity>,
        linearAdapter: StudentLinearAdapter,
        gridAdapter: StudentGridAdapter
    ) {
        fireStore.collection("Student").addSnapshotListener { value, _ ->
            if (value != null) {
                for (dc in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val stInFor = dc.document.toObject(StudentEntity::class.java)
                        stInFor.id = dc.document.id
                        studentList.add(stInFor)
                    }
                }
            }
            linearAdapter.submitList(studentList)
            gridAdapter.submitList(studentList)
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