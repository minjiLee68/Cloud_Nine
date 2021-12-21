package com.sophia.project_minji.chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.sophia.project_minji.entity.FollowUser
import com.sophia.project_minji.entity.User
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
        val usersId = preferenceManager.getString("followingId")
        viewModel.getFollowUsers(users, usersId).observe(viewLifecycleOwner, {
            userAdapter.submitList(it)
        })
    }

//        database.collection(Constants.kEY_COLLECTION_UESRS)
//            .get()
//            .addOnCompleteListener { task ->
//                loading(false)
//                val currentUserId = preferenceManager.getString(Constants.KEY_UESR_ID)
//                if (task.isSuccessful && task.result != null) {
//                    val users: ArrayList<User> = ArrayList()
//                    for (queryDocumentSnapshot: QueryDocumentSnapshot in task.result!!) {
//                        if (currentUserId == queryDocumentSnapshot.id) {
//                            continue
//                        }
//                        val user = User()
//
//                        user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME)!!
//                        user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)!!
//                        user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)!!
//                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN).toString()
//                        user.id = queryDocumentSnapshot.id
//
//                        users.add(user)
//                    }
//                    userAdapter = UsersAdapter(users,this)
//                    binding.userRecyclerView.adapter = userAdapter
//                    binding.userRecyclerView.visibility = View.VISIBLE
//                    binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
//                    userAdapter.submitList(users)
//                } else {
//                    showErrorMessage()
//                }
//           }

//    private fun getToken() {
//        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
//    }
//
//    private fun updateToken(token: String) {
//        val documentReference: DocumentReference =
//            firestore.collection("Users").document(uid)
//        documentReference.update("token", token)
//    }

//    override fun onUserClicked(user: User) {
//        val intent = Intent(requireContext().applicationContext, ChatActivity::class.java)
//        intent.putExtra(Constants.KEY_USER, user)
//        startActivity(intent)
//    }

//    private fun signOut() {
//        showToast("Signing out...")
//        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
//        val documentReference: DocumentReference =
//            database.collection(Constants.kEY_COLLECTION_UESRS).document(
//                preferenceManager.getString(Constants.KEY_UESR_ID)
//            )
//        val updates: HashMap<String, Any> = HashMap()
//        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
//        documentReference.update(updates)
//            .addOnSuccessListener {
//                preferenceManager.clear()
//                val intent = Intent(activity, SignInActivity::class.java)
//                startActivity(intent)
//            }
//            .addOnFailureListener { showToast("Unable to sign out") }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}