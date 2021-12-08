package com.sophia.project_minji.studentinfor

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sophia.project_minji.MainActivity
import com.sophia.project_minji.databinding.ActivityAddStudentBinding
import com.sophia.project_minji.listeners.CallAnotherActivityNavigator
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.lang.Exception

class AddStudentActivity : AppCompatActivity(), CallAnotherActivityNavigator {

    private lateinit var binding: ActivityAddStudentBinding
    private lateinit var selectImage: Uri

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListener()
        buttonUpload()
        phNumberFormat()
    }

    private fun setListener() {
        binding.myImag.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra("crop", true) //기존 코드에 이 줄 추가!
            fileterActivityLauncher.launch(intent)
            binding.textAddImage.visibility = View.GONE
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
                    selectImage = it.data?.data!!
                    binding.myImag.setImageURI(selectImage)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(it.data)
                    if (it.resultCode == Activity.RESULT_OK) {
                        result.uri?.let {
                            binding.myImag.setImageBitmap(result.bitmap)
                            binding.myImag.setImageURI(result.uri)
                            selectImage = result.uri
                        }
                    } else if (it.resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun buttonUpload() {
        binding.saveBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.let {
                viewModel.register(
                    it.stname.text.toString(),
                    it.stbrith.text.toString(),
                    it.stphnumber.text.toString(),
                    selectImage,
                    it.stcharacter.text.toString(),
                    this
                )
            }
            binding.stname.text?.clear()
            binding.stbrith.text?.clear()
            binding.stphnumber.text?.clear()
            binding.stcharacter.text?.clear()
            Glide.with(binding.myImag).clear(binding.myImag)
        }
    }

    private fun phNumberFormat() {
        val phnumber = binding.stphnumber
        phnumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

    override fun callActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }


//    private fun requestPermissions() {
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//            Log.d("권한요청", "$it")
//        }.launch(PERMISSIONS_REQUESTED)
//    }
//
//    companion object {
//        private const val PERMISSION_READ_EXTEDNAL_STORAGE =
//            android.Manifest.permission.READ_EXTERNAL_STORAGE
//        private const val PERMISSION_WRITE_EXTEDNAL_STORAGE =
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//
//        private val PERMISSIONS_REQUESTED: Array<String> = arrayOf(
//            PERMISSION_READ_EXTEDNAL_STORAGE,
//            PERMISSION_WRITE_EXTEDNAL_STORAGE
//        )
//    }
}