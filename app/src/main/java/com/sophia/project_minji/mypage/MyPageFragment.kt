package com.sophia.project_minji.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.UserPageActivity
import com.sophia.project_minji.ProfileSetUpActivity
import com.sophia.project_minji.adapter.FollowUserAdapter
import com.sophia.project_minji.adapter.UsersAdapter
import com.sophia.project_minji.databinding.MypageFragmentBinding
import com.sophia.project_minji.entity.FollowDto
import com.sophia.project_minji.entity.FollowUser
import com.sophia.project_minji.entity.User
import com.sophia.project_minji.utillties.PreferenceManager
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory

class MyPageFragment : Fragment() {

    private var _binding: MypageFragmentBinding? = null
    val binding: MypageFragmentBinding
        get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var userAdapter: FollowUserAdapter
    private val users: ArrayList<User> = ArrayList()

    private lateinit var preferenceManager: PreferenceManager

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MypageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        loadUserDetails()
        setListener()
        initRecyclerView()
        getUserObserver()
        followers()
    }

    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        preferenceManager = PreferenceManager(requireContext())
    }

    private fun setListener() {
        binding.profileSetting.setOnClickListener {
            val intent =
                Intent(requireContext().applicationContext, ProfileSetUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        userAdapter = FollowUserAdapter(users)
        binding.userRecyclerView.adapter = userAdapter
        binding.userRecyclerView.visibility = View.VISIBLE
        binding.userRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun getUserObserver() {
        viewModel.getUsers(users).observe(viewLifecycleOwner, {
            userAdapter.submitList(it)
        })
    }

    private fun loadUserDetails() {
        firestore.collection("Users").document(uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val name = task.result!!.getString("name")
                        val image = task.result!!.getString("image")
                        binding.nickName.text = name
                        Glide.with(requireContext()).load(image).into(binding.myProfile)
                    }
                }
            }
    }

    private fun followers() {
        firestore.collection("follow").document(uid).addSnapshotListener { value, _ ->
            if (value != null) {
                val followDto = value.toObject(FollowDto::class.java)
                if (followDto?.followingCount != null) {
                    binding.following.text = followDto.followingCount.toString()
                } else if (followDto?.followerCount != null) {
                    binding.follower.text = followDto.followerCount.toString()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}