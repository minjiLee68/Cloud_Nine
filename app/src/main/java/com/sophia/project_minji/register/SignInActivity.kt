package com.sophia.project_minji.register

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.MainActivity
import com.sophia.project_minji.databinding.ActivitySignInBinding
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferences: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager(applicationContext)

        if (preferences.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListener()
    }

    private fun setListener() {
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener { signIn() }
    }

    private fun signIn() {
        loading(true)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection(Constants.kEY_COLLECTION_UESRS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.etEmail.text.toString())
            .whereEqualTo(Constants.KEY_PHNUMBER, binding.etPhnumber.text.toString())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                    val documentSnapshot: DocumentSnapshot = task.result!!.documents[0]
                    preferences.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferences.putString(Constants.KEY_UESR_ID, documentSnapshot.id)
                    preferences.putString(
                        Constants.KEY_EMAIL,
                        documentSnapshot.getString(Constants.KEY_EMAIL)!!
                    )
                    preferences.putString(
                        Constants.KEY_NAME,
                        documentSnapshot.getString(Constants.KEY_NAME)!!
                    )
                    preferences.putString(
                        Constants.KEY_IMAGE,
                        documentSnapshot.getString(Constants.KEY_IMAGE)!!
                    )
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    Log.d(ContentValues.TAG,"NAME ${preferences.getString(Constants.KEY_NAME)}")
                    Log.d(ContentValues.TAG,"EMAIL ${preferences.getString(Constants.KEY_EMAIL)}")
                } else {
                    loading((false))
                    showToast("Unable to sign in")
                }
            }
    }


    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignIn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.buttonSignIn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}