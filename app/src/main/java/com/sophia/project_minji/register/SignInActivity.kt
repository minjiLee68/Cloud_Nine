package com.sophia.project_minji.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.MainActivity
import com.sophia.project_minji.databinding.ActivitySignInBinding
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        setListener()
//        if (preferences.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
//            val intent = Intent(applicationContext, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(mAuth.currentUser)
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setListener() {
        binding.textCreateNewAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
        }

//        loading(true)
//        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
//        database.collection(Constants.kEY_COLLECTION_UESRS)
//            .whereEqualTo(Constants.KEY_EMAIL, binding.etEmail.text.toString())
//            .whereEqualTo(Constants.KEY_PHNUMBER, binding.etPhnumber.text.toString())
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
//                    val documentSnapshot: DocumentSnapshot = task.result!!.documents[0]
//                    preferences.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
//                    preferences.putString(Constants.KEY_UESR_ID, documentSnapshot.id)
//                    preferences.putString(
//                        Constants.KEY_EMAIL,
//                        documentSnapshot.getString(Constants.KEY_EMAIL)!!
//                    )
//                    preferences.putString(
//                        Constants.KEY_NAME,
//                        documentSnapshot.getString(Constants.KEY_NAME)!!
//                    )
//                    preferences.putString(
//                        Constants.KEY_IMAGE,
//                        documentSnapshot.getString(Constants.KEY_IMAGE)!!
//                    )
//                    val intent = Intent(applicationContext, MainActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    startActivity(intent)
//                } else {
//                    loading((false))
//                    showToast("Unable to sign in")
//                }
//            }
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