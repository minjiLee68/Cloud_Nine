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
//        firestore.collection("Users").document(uid).get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    if (task.result!!.exists()) {
//                        val name = task.result!!.getString("name")!!
//                        val image = task.result!!.getString("image")!!
//                        val chats = Chat(binding.inputMessage.text.toString(),time,userId,uid, image)
//                        firestore.collection("Posts/$userId/Comments").document().set(chats)
//                    }
//                }
//            }

//        firestore.collection("Users/$userId/Chats").addSnapshotListener { value, _ ->
//            for (dc: DocumentChange in value!!.documentChanges) {
//                if (dc.type == DocumentChange.Type.ADDED) {
//                    val chats = dc.document.toObject(Chat::class.java)
//                    chats.message = dc.document.getString("message")!!
//                    chats.sendId = dc.document.getString("sendId")!!
//                    chats.receiverId = dc.document.getString("receiverId")!!
//                    chats.userProfile = dc.document.getString("userProfile")!!
//                    chats.time = dc.document.getString("time")!!
//
//                    chatMessages.add(chats)
//                    chatAdapter.submitList(chatMessages)
//                }
//            }
//        }

//        val message: HashMap<String,Any> = HashMap()
//        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_UESR_ID)
//        message[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
//        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
//        message[Constants.KEY_TIMESTAMP] = Date()
//        database.collection(Constants.KEY_COLLECTION).add(message)
//
//        if (conversionId != null) {
//            updateConversion(binding.inputMessage.text.toString())
//        } else {
//            val conversion: HashMap<String, Any> = HashMap()
//            conversion[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_UESR_ID)
//            conversion[Constants.KEY_SENDER_NAME] = preferenceManager.getString(Constants.KEY_NAME)
//            conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE)
//            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
//            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name!!
//            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser.image!!
//            conversion[Constants.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
//            conversion[Constants.KEY_TIMESTAMP] = Date()
//        }
//        binding.inputMessage.text = null
    }

//    private fun listenMessages() {
//        firestore.collection(Constants.KEY_COLLECTION)
//            .whereEqualTo("sendId","receiverId")
//            .whereEqualTo("receiverId", "sendId")
//            .addSnapshotListener(eventListener)
//        firestore.collection(Constants.KEY_COLLECTION)
//            .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
//            .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_UESR_ID))
//            .addSnapshotListener(eventListener)
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    private val eventListener: EventListener<QuerySnapshot> = EventListener { value, error ->
//        if (error != null) {
//            return@EventListener
//        }
//        if (value != null) {
//            val count = chatMessages.size
//            for (documentChange: DocumentChange in value.documentChanges) {
//                if (documentChange.type == DocumentChange.Type.ADDED) {
//                    val chatMessage = ChatMessage()
//                    chatMessage.senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
//                    chatMessage.receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
//                    chatMessage.message = documentChange.document.getString(Constants.KEY_MESSAGE)
//                    chatMessage.dateTime = getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!)
//                    chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
//                    chatMessages.add(chatMessage)
//                }
//            }
//            Collections.sort(chatMessages, Comparator { obj1, obj2 -> obj1.dateObject!!.compareTo(obj2.dateObject) })
//            if (count == 0) {
//                chatAdapter.notifyDataSetChanged()
//            } else {
//                chatAdapter.notifyItemRangeInserted(chatMessages.size,chatMessages.size)
//                binding.chatRecyclerview.smoothScrollToPosition(chatMessages.size -1)
//            }
//            binding.chatRecyclerview.visibility = View.VISIBLE
//        }
//        binding.progressBar.visibility = View.GONE
//    }

    private fun loadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }

//    private fun setListener() {
//        binding.imageBack.setOnClickListener { onBackPressed() }
//        binding.layoutSend.setOnClickListener { sendMessage() }
//    }

//    private fun getReadableDateTime(date: Date): String {
//        return SimpleDateFormat("hh:mm a",Locale.getDefault()).format(date)
//    }
//
//    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap {
//        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
//        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
//    }
//
//    private fun updateConversion(message: String) {
//        val documentReference: DocumentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId!!)
//        documentReference.update(
//            Constants.KEY_LAST_MESSAGE,message,
//            Constants.KEY_TIMESTAMP,Date()
//        )
//
//    }
}