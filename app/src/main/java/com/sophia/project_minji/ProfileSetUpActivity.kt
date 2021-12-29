package com.sophia.project_minji

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sophia.project_minji.databinding.ActivityProfileSetUpBinding
import com.sophia.project_minji.listeners.CallAnotherActivityNavigator
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class ProfileSetUpActivity : AppCompatActivity(), CallAnotherActivityNavigator {

    private lateinit var binding: ActivityProfileSetUpBinding
    private lateinit var mImageUri: Uri
    private var isPhotoSelected: Boolean = false

    private lateinit var storageReference: StorageReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var Uid: String

    private val viewmodel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        circleImageClick()
        requestPermissions()
        saveBtnClick()
        getProfileInFor()
    }

    private fun init() {
        storageReference = FirebaseStorage.getInstance().reference
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        Uid = auth.currentUser?.uid.toString()
    }

    private fun getProfileInFor() {
        firestore.collection("Users").document(Uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val name = task.result!!.getString("name")
                        val image = task.result!!.getString("image")
                        val school = task.result!!.getString("school")
                        val introduce = task.result!!.getString("introduce")
                        val webSite = task.result!!.getString("webSite")
                        binding.etUserName.setText(name)
                        binding.etUserSchool.setText(school)
                        binding.etUserIntroduce.setText(introduce)
                        binding.etUserWebSite.setText(webSite)
                        mImageUri = Uri.parse(image)
                        Glide.with(this).load(image).into(binding.profile)
                    }
                }
            }
    }

    private fun saveBtnClick() {
        binding.save.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val userName = binding.etUserName.text.toString()
            val userSchool = binding.etUserSchool.text.toString()
            val introduce = binding.etUserIntroduce.text.toString()
            val webSite = binding.etUserWebSite.text.toString()
            viewmodel.userProfile(userName,userSchool,introduce,webSite,isPhotoSelected, mImageUri, this)
        }
    }

    private fun circleImageClick() {
        binding.changeProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra("crop", true)
            fileterActivityLauncher.launch(intent)
        }
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private val fileterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                RESULT_OK -> {
                    it.data?.data?.let { uri ->
                        cropImage(uri)
                    }
                    mImageUri = it.data?.data!!
                    binding.profile.setImageURI(mImageUri)
                    isPhotoSelected = true
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(it.data)
                    if (it.resultCode == Activity.RESULT_OK) {
                        result.uri?.let {
                            binding.profile.setImageBitmap(result.bitmap)
                            binding.profile.setImageURI(result.uri)
                            mImageUri = result.uri
                        }
                    } else if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
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
            Manifest.permission.READ_EXTERNAL_STORAGE
        private const val PERMISSION_WRITE_EXTEDNAL_STORAGE =
            Manifest.permission.WRITE_EXTERNAL_STORAGE

        private val PERMISSIONS_REQUESTED: Array<String> = arrayOf(
            PERMISSION_READ_EXTEDNAL_STORAGE,
            PERMISSION_WRITE_EXTEDNAL_STORAGE
        )
    }

    override fun callActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}