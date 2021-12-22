package com.sophia.project_minji.studentinfor

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sophia.project_minji.databinding.StudentInforActivityBinding
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory

class StudentInforActivity : AppCompatActivity() {

    private lateinit var binding: StudentInforActivityBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var auth: FirebaseAuth
    private lateinit var Uid: String
    private var isPhotoSelected: Boolean = false

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StudentInforActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        setDocument()
        setListener()
    }

    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()
        Uid = auth.currentUser?.uid.toString()
    }

    private fun setListener() {
        binding.update.setOnClickListener { setUpdate() }
        binding.save.setOnClickListener { updateButtonClick() }
    }

    private fun setDocument() {
        val id = intent.getStringExtra("id").toString()

        firestore.collection("Students").document(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result!!.exists()) {
                    val name = task.result!!.getString("name")
                    val birth = task.result!!.getString("birth")
                    val phNumber = task.result!!.getString("phNumber")
                    val image = task.result!!.getString("image")
                    val character = task.result!!.getString("character")

                    binding.stName.text = name
                    binding.stBirth.text = birth
                    binding.stPhnumber.text = phNumber
                    binding.stCharacter.text = character
                    Glide.with(this).load(image).into(binding.stprofile)
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