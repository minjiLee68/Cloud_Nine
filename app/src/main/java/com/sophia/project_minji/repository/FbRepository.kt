package com.sophia.project_minji.repository

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.sophia.project_minji.MainActivity
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.adapter.UsersAdapter
import com.sophia.project_minji.dataclass.User
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import kotlin.collections.ArrayList

class FbRepository() {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    @SuppressLint("SimpleDateFormat")
    fun register(
        name: String,
        birth: String,
        phNumber: String,
        image: String,
        character: String
    ) {
//        val student = StudentEntity(name, birth, phNumber, image, character)
//        fireStore.collection("Student").document().set(student)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: HashMap<String, Any> = HashMap()
        user[Constants.KEY_STUDENT_NAME] = name
        user[Constants.KEY_STUDENT_BIRTH] = birth
        user[Constants.KEY_STUDENT_PHNUMBER] = phNumber
        user[Constants.KEY_STUDENT_IMAGE] = image
        user[Constants.KEY_STUDENT_CHARACTER] = character

        database.collection(Constants.KEY_COLLECTION_STUDENT).document().set(user)

    }

    fun setStudentInFor(
        studentList: ArrayList<StudentEntity>,
        linearAdapter: StudentLinearAdapter,
        gridAdapter: StudentGridAdapter
    ) {

        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_STUDENT)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    for (queryDocumentSnapshot: QueryDocumentSnapshot in task.result!!) {
                        val student = StudentEntity()

                        student.name = queryDocumentSnapshot.getString(Constants.KEY_STUDENT_NAME)
                        student.brith = queryDocumentSnapshot.getString(Constants.KEY_STUDENT_BIRTH)
                        student.image = queryDocumentSnapshot.getString(Constants.KEY_STUDENT_IMAGE)
                        student.phNumber = queryDocumentSnapshot.getString(Constants.KEY_STUDENT_PHNUMBER)
                        student.character = queryDocumentSnapshot.getString(Constants.KEY_STUDENT_CHARACTER)
                        student.id = queryDocumentSnapshot.id

                        studentList.add(student)
                    }
                    linearAdapter.submitList(studentList)
                    gridAdapter.submitList(studentList)
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