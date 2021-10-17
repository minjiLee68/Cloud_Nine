package com.sophia.project_minji.studentinfor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sophia.project_minji.databinding.StudentInforActivityBinding
import com.sophia.project_minji.entity.StudentEntity

class StudentInforActivity : AppCompatActivity() {

    private lateinit var binding: StudentInforActivityBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StudentInforActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        setDocument()
    }

    private fun setDocument() {
        val id = intent.getStringExtra("id")

        if (id != null) {
            firestore.collection("Student").document(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val student = task.result?.toObject(StudentEntity::class.java)
                    Glide.with(this).load(student?.image).into(binding.stprofile)
                    binding.stName.text = student?.name
                    binding.stBirth.text = student?.brith
                    binding.stPhnumber.text = student?.phNumber
                    binding.stCharacter.text = student?.character
                }
            }
        }
    }
}