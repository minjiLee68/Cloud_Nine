package com.sophia.project_minji.studentinfor

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sophia.project_minji.databinding.StudentInforActivityBinding
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.utillties.Constants

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
        setListener()
    }

    private fun setListener() {
        binding.update.setOnClickListener { setUpdate() }
    }

    private fun setDocument() {
        val id = intent.getStringExtra("id")

        if (id != null) {
            firestore.collection(Constants.KEY_COLLECTION_STUDENT).document(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val student = task.result?.toObject(StudentEntity::class.java)
                    binding.stName.text = student?.name
                    binding.stBirth.text = student?.brith
                    binding.stPhnumber.text = student?.phNumber
                    binding.stCharacter.text = student?.character
                    Glide.with(this).load(student?.image).into(binding.stprofile)
                }

            }
        }
    }

    private fun setUpdate() {
        binding.apply {
            stNameUpdate.visibility = View.VISIBLE
            stName.visibility = View.GONE
            stBirthUpdate.visibility = View.VISIBLE
            stBirth.visibility = View.GONE
            stPhnumberUpdate.visibility = View.VISIBLE
            stPhnumber.visibility = View.GONE
            stCharacterUpdate.visibility = View.VISIBLE
            stCharacter.visibility = View.GONE

            stNameUpdate.setText(stName.text.toString())
            stBirthUpdate.setText(stBirth.text.toString())
            stPhnumberUpdate.setText(stPhnumber.text.toString())
            stCharacterUpdate.setText(stCharacter.text.toString())
        }
    }

}