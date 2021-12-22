package com.sophia.project_minji.chats

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
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.register.SignInActivity
import com.sophia.project_minji.adapter.UsersAdapter
import com.sophia.project_minji.databinding.ChatsFragmentBinding
import com.sophia.project_minji.dataclass.FollowUser
import com.sophia.project_minji.utillties.PreferenceManager
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory

class ChatsFragment : Fragment() {

    private var _binding: ChatsFragmentBinding? = null
    val binding: ChatsFragmentBinding
        get() = _binding!!

    private lateinit var userAdapter: UsersAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private val users: ArrayList<FollowUser> = ArrayList()
    private lateinit var preferenceManager: PreferenceManager

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setButtonLogout()
        loadUserDetails()
        initRecyclerView()
        getUserObserver()
    }

    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        preferenceManager = PreferenceManager(requireContext())
    }

    private fun setButtonLogout() {
        binding.imageSignOut.setOnClickListener {
            val intent = Intent(requireContext().applicationContext, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth.signOut()
        }
    }

    private fun loadUserDetails() {
        firestore.collection("Users").document(uid).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val image = task.result!!.getString("image")!!
                        val name = task.result!!.getString("name")!!
                        binding.textName.text = name
                        Glide.with(requireContext()).load(image).into(binding.imageProfile)
                    }
                }
            }
    }

    private fun initRecyclerView() {
        userAdapter = UsersAdapter(users)
        binding.userRecyclerView.adapter = userAdapter
        binding.userRecyclerView.visibility = View.VISIBLE
        binding.userRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun getUserObserver() {
        viewModel.getFollowUsers(users).observe(viewLifecycleOwner, {
            userAdapter.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}