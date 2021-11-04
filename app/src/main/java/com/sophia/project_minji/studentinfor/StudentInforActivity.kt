package com.sophia.project_minji.studentinfor

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sophia.project_minji.databinding.StudentInforActivityBinding
import com.sophia.project_minji.entity.StudentEntity
import com.sophia.project_minji.listeners.OnItemClickListener
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory

class StudentInforActivity : AppCompatActivity() {

    private lateinit var binding: StudentInforActivityBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory()
    }

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
        binding.save.setOnClickListener { updateButtonClick() }
    }

    private fun setDocument() {
        val id = intent.getStringExtra("id")
        Log.d(ContentValues.TAG,"STUDENT${id}")

        if (id != null) {
            firestore.collection(Constants.KEY_COLLECTION_STUDENT).document(id).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val student = task.result?.toObject(StudentEntity::class.java)
                    binding.stName.text = student?.name
                    binding.stBirth.text = student?.birth
                    binding.stPhnumber.text = student?.phNumber
                    binding.stCharacter.text = student?.character
                    Glide.with(this).load(student?.image).into(binding.stprofile)
                }

            }
        }
    }

    private fun setUpdate() {
        binding.apply {
            save.visibility = View.VISIBLE
            update.visibility = View.GONE
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

    private fun updateButtonClick() {
        val id = intent.getStringExtra("id")

        viewModel.updateStudent(
            id.toString(),
            binding.stNameUpdate.text.toString(),
            binding.stBirthUpdate.text.toString(),
            binding.stPhnumberUpdate.text.toString(),
            binding.stCharacterUpdate.text.toString()
        )
        binding.apply {
            save.visibility = View.GONE
            update.visibility = View.VISIBLE
            stNameUpdate.visibility = View.GONE
            stName.visibility = View.VISIBLE
            stBirthUpdate.visibility = View.GONE
            stBirth.visibility = View.VISIBLE
            stPhnumberUpdate.visibility = View.GONE
            stPhnumber.visibility = View.VISIBLE
            stCharacterUpdate.visibility = View.GONE
            stCharacter.visibility = View.VISIBLE

            stName.text = stNameUpdate.text.toString()
            stBirth.text = stBirthUpdate.text.toString()
            stPhnumber.text = stPhnumberUpdate.text.toString()
            stCharacter.text = stCharacterUpdate.text.toString()
        }
    }
}