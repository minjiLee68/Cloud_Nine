package com.sophia.project_minji.repository

import android.app.Application
import android.net.Uri
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sophia.project_minji.adapter.StudentGridAdapter
import com.sophia.project_minji.adapter.StudentLinearAdapter
import com.sophia.project_minji.entity.StudentEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FbRepository() {

    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    //firebase
    fun register(name: String, birth: String, phnumber: String, image: Uri, character: String) {
        val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        storage.getReference().child("Student").child(fileName)
            .putFile(image)
            .addOnSuccessListener { taskSnapshot ->

                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                    val imageUri = it.toString()
                    val student = StudentEntity(name, birth, phnumber, imageUri, character)
                    firestore.collection("Student")
                        .document().set(student)
                }
            }
    }

    fun setFirestore(
        studentList: ArrayList<StudentEntity>,
        Ladapter: StudentLinearAdapter,
        Gadapter: StudentGridAdapter
    ) {
        firestore.collection("Student").addSnapshotListener { value, error ->
            if (value != null) {
                for (dc in value.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val stinfor = dc.document.toObject(StudentEntity::class.java)
                        stinfor.id = dc.document.id
                        studentList.add(stinfor)
                    }
                }
                Gadapter.submitList(studentList)
                Ladapter.submitList(studentList)
            }
        }
    }

    fun deleteStudent(position: Int) {
        firestore.collection("Student").get().addOnCompleteListener {
            if (it.isSuccessful) {
                val documentSnapshot: DocumentSnapshot = it.getResult()!!.documents.get(position)
                val documentId = documentSnapshot.id
                firestore.collection("Student")
                    .document(documentId)
                    .delete()
            }
        }
    }
}