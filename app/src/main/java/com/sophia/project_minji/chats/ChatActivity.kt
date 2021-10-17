package com.sophia.project_minji.chats

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.sophia.project_minji.adapter.ChatAdapter
import com.sophia.project_minji.databinding.ActivityChatBinding
import com.sophia.project_minji.dataclass.ChatMessage
import com.sophia.project_minji.dataclass.User
import com.sophia.project_minji.utillties.Constants
import com.sophia.project_minji.utillties.PreferenceManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var receiverUser: User
    private lateinit var chatMessages: ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var database: FirebaseFirestore
    private var conversionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListener()
        loadReceiverDetails()
        init()
        listenMessages()
    }

    private fun init() {
        preferenceManager = PreferenceManager(applicationContext)
        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            chatMessages,
            getBitmapFromEncodedString(receiverUser.image!!),
            preferenceManager.getString(Constants.KEY_UESR_ID)
        )
        binding.chatRecyclerview.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()
    }

    private fun sendMessage() {
        val message: HashMap<String,Any> = HashMap()
        message[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_UESR_ID)
        message[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        database.collection(Constants.KEY_COLLECTION).add(message)

        if (conversionId != null) {
            updateConversion(binding.inputMessage.text.toString())
        } else {
            val conversion: HashMap<String, Any> = HashMap()
            conversion[Constants.KEY_SENDER_ID] = preferenceManager.getString(Constants.KEY_UESR_ID)
            conversion[Constants.KEY_SENDER_NAME] = preferenceManager.getString(Constants.KEY_NAME)
            conversion[Constants.KEY_SENDER_IMAGE] = preferenceManager.getString(Constants.KEY_IMAGE)
            conversion[Constants.KEY_RECEIVER_ID] = receiverUser.id!!
            conversion[Constants.KEY_RECEIVER_NAME] = receiverUser.name!!
            conversion[Constants.KEY_RECEIVER_IMAGE] = receiverUser.image!!
            conversion[Constants.KEY_LAST_MESSAGE] = binding.inputMessage.text.toString()
            conversion[Constants.KEY_TIMESTAMP] = Date()
//            addConversion(conversion)
        }
        binding.inputMessage.text = null
    }

    private fun listenMessages() {
        database.collection(Constants.KEY_COLLECTION)
            .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_UESR_ID))
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION)
            .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_UESR_ID))
            .addSnapshotListener(eventListener)
    }

    private val eventListener: EventListener<QuerySnapshot> = EventListener { value, error ->
        if (error != null) {
            return@EventListener
        }
        if (value != null) {
            val count = chatMessages.size
            for (documentChange: DocumentChange in value.documentChanges) {
                if (documentChange.type == DocumentChange.Type.ADDED) {
                    val chatMessage = ChatMessage()
                    chatMessage.senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                    chatMessage.receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                    chatMessage.message = documentChange.document.getString(Constants.KEY_MESSAGE)
                    chatMessage.dateTime = getReadableDateTime(documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!)
                    chatMessage.dateObject = documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                    chatMessages.add(chatMessage)
                }
            }
            Collections.sort(chatMessages, Comparator { obj1, obj2 -> obj1.dateObject!!.compareTo(obj2.dateObject) })
            if (count == 0) {
                chatAdapter.notifyDataSetChanged()
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size,chatMessages.size)
                binding.chatRecyclerview.smoothScrollToPosition(chatMessages.size -1)
            }
            binding.chatRecyclerview.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun loadReceiverDetails() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_USER) as User
        binding.textName.text = receiverUser.name
    }

    private fun setListener() {
        binding.imageBack.setOnClickListener { onBackPressed() }
        binding.layoutSend.setOnClickListener { sendMessage() }
    }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("hh:mm a",Locale.getDefault()).format(date)
    }

    private fun getBitmapFromEncodedString(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes,0,bytes.size)
    }

//    private fun addConversion(conversion: HashMap<String,Any>) {
//        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
//            .add(conversion)
//            .addOnSuccessListener { documentReference ->
//                conversionId = documentReference.id
//            }
//    }

    private fun updateConversion(message: String) {
        val documentReference: DocumentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId!!)
        documentReference.update(
            Constants.KEY_LAST_MESSAGE,message,
            Constants.KEY_TIMESTAMP,Date()
        )

    }
}