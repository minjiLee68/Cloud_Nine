package com.sophia.project_minji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.databinding.IntroduceFragmentBinding

class IntroduceFragment : Fragment() {

    private var _binding: IntroduceFragmentBinding? = null
    val binding: IntroduceFragmentBinding
        get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = IntroduceFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        userIntroduce()
    }

    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
    }

    private fun userIntroduce() {
        firestore.collection("Users").document(uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val school = task.result!!.getString("school")
                        val introduce = task.result!!.getString("introduce")
                        val webSite = task.result!!.getString("webSite")
                        binding.tvUserSchool.text = school
                        binding.tvUserIntroduece.text = introduce
                        binding.tvUserWebSite.text = webSite
                    }
                }
            }
    }
}