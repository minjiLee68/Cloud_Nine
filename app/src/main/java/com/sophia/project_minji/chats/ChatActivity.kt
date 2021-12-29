package com.sophia.project_minji.chats

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.sophia.project_minji.adapter.ChatAdapter
import com.sophia.project_minji.databinding.ActivityChatBinding
import com.sophia.project_minji.dataclass.Chat
import com.sophia.project_minji.dataclass.User
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import com.sophia.project_minji.viewmodel.FirebaseViewModel
import com.sophia.project_minji.viewmodel.FirebaseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User
    private lateinit var chatMessages: ArrayList<Chat>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var userId: String

    private val now = System.currentTimeMillis()
    private val date = Date(now)

    @SuppressLint("SimpleDateFormat")
    private val time = SimpleDateFormat("hh:mm a").format(date)

    private val viewModel by viewModels<FirebaseViewModel> {
        FirebaseViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId").toString()

        init()
        initRecyclerView()
        sendMessage()
        sendComment()
    }

    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = ArrayList()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()

        binding.imageBack.setOnClickListener { onBackPressed() }
    }

    private fun initRecyclerView() {
        val image = preferenceManager.getString("profile")
        chatAdapter = ChatAdapter(chatMessages, image, uid)
        binding.chatRecyclerview.adapter = chatAdapter
        binding.chatRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun sendComment() {
        val name = preferenceManager.getString("nickName")
        binding.textName.text = name
        binding.layoutSend.setOnClickListener {
            viewModel.sendMessage(binding.inputMessage.text.toString(), time, userId)
            binding.inputMessage.text = null
        }
    }

    private fun sendMessage() {
        viewModel.getMessage(userId, chatMessages).observe(this, { live ->
            chatAdapter.submitList(live)
            chatAdapter.notifyItemRangeInserted(live.size, live.size)
            binding.chatRecyclerview.smoothScrollToPosition(live.size - 1)
        })
    }

    private fun loadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }
}