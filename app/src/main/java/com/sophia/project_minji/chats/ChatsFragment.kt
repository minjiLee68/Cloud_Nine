package com.sophia.project_minji.chats

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.messaging.FirebaseMessaging
import com.sophia.project_minji.register.SignInActivity
import com.sophia.project_minji.adapter.UsersAdapter
import com.sophia.project_minji.databinding.ChatsFragmentBinding
import com.sophia.project_minji.dataclass.User
import com.sophia.project_minji.listeners.UserListener
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager

class ChatsFragment: Fragment(), UserListener {

    private var _binding: ChatsFragmentBinding? = null
    val binding: ChatsFragmentBinding
    get() = _binding!!

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var userAdapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatsFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())

        getUsers()
        getToken()
        loadUserDetails()
        setListener()
    }

    private fun setListener() {
        binding.imageSignOut.setOnClickListener { signOut() }
    }

    private fun showErrorMessage() {
        binding.textErrorMessage.text = String.format("No user available")
        binding.textErrorMessage.visibility = View.VISIBLE
    }

    private fun loadUserDetails() {
        binding.textName.text = preferenceManager.getString(Constants.KEY_NAME)
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun getUsers() {
        loading(true)
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        database.collection(Constants.kEY_COLLECTION_UESRS)
            .get()
            .addOnCompleteListener { task ->
                loading(false)
                val currentUserId = preferenceManager.getString(Constants.KEY_UESR_ID)
                if (task.isSuccessful && task.result != null) {
                    val users: ArrayList<User> = ArrayList()
                    for (queryDocumentSnapshot: QueryDocumentSnapshot in task.result!!) {
                        if (currentUserId == queryDocumentSnapshot.id) {
                            continue
                        }
                        val user = User()

                        user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME)!!
                        user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL)!!
                        user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE)!!
                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN).toString()
                        user.id = queryDocumentSnapshot.id

                        users.add(user)
                    }
                    userAdapter = UsersAdapter(users,this)
                    binding.userRecyclerView.adapter = userAdapter
                    binding.userRecyclerView.visibility = View.VISIBLE
                    binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                    userAdapter.submitList(users)
                } else {
                    showErrorMessage()
                }
            }
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener(this::updateToken)
    }

    private fun updateToken(token: String) {
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference = database.collection(Constants.kEY_COLLECTION_UESRS)
            .document(preferenceManager.getString(Constants.KEY_UESR_ID))
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
            .addOnFailureListener { showToast("Unable to update error") }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
    }

    override fun onUserClicked(user: User) {
        val intent = Intent(requireContext().applicationContext, ChatActivity::class.java)
        intent.putExtra(Constants.KEY_USER,user)
        startActivity(intent)
    }

    private fun signOut() {
        showToast("Signing out...")
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val documentReference: DocumentReference =
            database.collection(Constants.kEY_COLLECTION_UESRS).document(
                preferenceManager.getString(Constants.KEY_UESR_ID)
            )
        val updates: HashMap<String, Any> = HashMap()
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(updates)
            .addOnSuccessListener {
                preferenceManager.clear()
                val intent = Intent(activity, SignInActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { showToast("Unable to sign out") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}