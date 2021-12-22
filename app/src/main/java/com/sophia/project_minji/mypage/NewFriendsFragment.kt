package com.sophia.project_minji.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.adapter.FollowUserAdapter
import com.sophia.project_minji.databinding.NewFriendsFragmentBinding
import com.sophia.project_minji.dataclass.User
import com.sophia.project_minji.utillties.PreferenceManager
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory

class NewFriendsFragment : Fragment() {

    private var _binding: NewFriendsFragmentBinding? = null
    val binding: NewFriendsFragmentBinding
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
    ): View? {
        _binding = NewFriendsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initRecyclerView()
        getUserObserver()
    }

    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        preferenceManager = PreferenceManager(requireContext())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}