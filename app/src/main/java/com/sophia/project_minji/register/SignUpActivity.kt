package com.sophia.project_minji.register

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.MainActivity
import com.sophia.project_minji.databinding.ActivitySignUpBinding
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var encodedImage: String

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(applicationContext)

        requestPermissions()
        setListeners()
    }

    private fun setListeners() {
        binding.textSignIn.setOnClickListener { onBackPressed() }
        binding.saveBtn.setOnClickListener {
            signUp()
        }
        binding.myImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            fileterActivityLauncher.launch(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun encodeImages(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight,false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private  val fileterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (it.data != null) {
                    val imageUri = it.data!!.data
                    try {
                        val inputStream = contentResolver.openInputStream(imageUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.myImage.setImageBitmap(bitmap)
                        binding.textAddImage.visibility = View.GONE
                        encodedImage = encodeImages(bitmap)
                    }catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private fun requestPermissions() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            Log.d("권한요청", "$it")
        }.launch(PERMISSIONS_REQUESTED)
    }

    companion object {
        private const val PERMISSION_READ_EXTEDNAL_STORAGE =
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_WRITE_EXTEDNAL_STORAGE =
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE

        private val PERMISSIONS_REQUESTED: Array<String> = arrayOf(
            PERMISSION_READ_EXTEDNAL_STORAGE,
            PERMISSION_WRITE_EXTEDNAL_STORAGE
        )
    }

    private fun signUp() {
        loading(true)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: HashMap<String,Any> = HashMap()
        user[Constants.KEY_NAME] = binding.myName.text.toString()
        user[Constants.KEY_EMAIL] = binding.myEmail.text.toString()
        user[Constants.KEY_PHNUMBER] = binding.myPhnumber.text.toString()
        user[Constants.KEY_IMAGE] = encodedImage
        database.collection(Constants.kEY_COLLECTION_UESRS) // missing or insufficient permissions
            .add(user)
            .addOnSuccessListener { documentReference ->
                loading(false)
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true)
                preferenceManager.putString(Constants.KEY_UESR_ID,documentReference.id)
                preferenceManager.putString(Constants.KEY_NAME,binding.myName.text.toString())
                preferenceManager.putString(Constants.KEY_IMAGE,encodedImage)
                preferenceManager.putString(Constants.KEY_EMAIL,binding.myEmail.text.toString())
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                loading(false)
                showToast(exception.message!!)
            }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.saveBtn.visibility = View.INVISIBLE
            binding.progressBar.isVisible = true
        } else {
            binding.progressBar.isVisible = false
            binding.saveBtn.visibility = View.VISIBLE
        }
    }
}