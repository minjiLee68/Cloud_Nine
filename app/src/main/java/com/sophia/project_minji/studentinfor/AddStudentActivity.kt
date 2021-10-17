package com.sophia.project_minji.studentinfor

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sophia.project_minji.activity.MainActivity
import com.sophia.project_minji.databinding.ActivityAddStudentBinding
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import java.lang.Exception

class AddStudentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStudentBinding

    var selectImage: Uri? = null

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permission()
        requestPermissions()
        buttonUpload()
        phNumberFormat()
    }

    private fun phNumberFormat() {
        val phnumber = binding.stphnumber
        phnumber.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

    private fun buttonUpload() {
        val students: HashMap<String, Any> = HashMap()
        binding.saveBtn.setOnClickListener {
            if (selectImage != null) {
                binding.let {
                    viewModel.register(
                        students,
                        it.stname.text.toString(),
                        it.stbrith.text.toString(),
                        it.stphnumber.text.toString(),
                        selectImage!!,
                        it.stcharacter.text.toString()
                    )
                }
            }
            binding.stname.text?.clear()
            binding.stbrith.text?.clear()
            binding.stphnumber.text?.clear()
            binding.stcharacter.text?.clear()
            Glide.with(binding.myImag).clear(binding.myImag)
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun permission() {
        val fileterActivityLauncher: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK && it.data?.data != null) {
                    selectImage = it.data?.data
                    binding.myImag.setImageURI(selectImage)
                    try {
                        selectImage?.let {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source =
                                    ImageDecoder.createSource(this.contentResolver, selectImage!!)
                                val bitmap = ImageDecoder.decodeBitmap(source)
                                binding.myImag.setImageBitmap(bitmap)
                                binding.textAddImage.visibility = View.GONE
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        binding.myImag.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            fileterActivityLauncher.launch(intent)
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
}