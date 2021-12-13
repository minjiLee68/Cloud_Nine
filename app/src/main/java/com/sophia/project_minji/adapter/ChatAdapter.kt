package com.sophia.project_minji.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sophia.project_minji.databinding.ItemContainerReceivedMessageBinding
import com.sophia.project_minji.databinding.ItemContainerSentMessageBinding
import com.sophia.project_minji.dataclass.ChatMessage
import com.sophia.project_minji.entity.Chat

class ChatAdapter(
    private val chatMessages: ArrayList<Chat>,
    private val profile: String?,
    private val senderId: String
) : ListAdapter<Chat, RecyclerView.ViewHolder>(

    object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean =
            oldItem.message == newItem.message && oldItem.time == newItem.time

    }

) {

    inner class SentMessageViewHolder(
        private val binding: ItemContainerSentMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(chatMessage: Chat) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.time
        }
    }

    inner class ReceivedMessageViewHolder(
        private val binding: ItemContainerReceivedMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(chatMessage: Chat, profile: String) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.time
            Glide.with(itemView.context)
                .load(profile)
                .into(binding.imageProfile)
        }
    }

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_SENT) {
            return SentMessageViewHolder(
                ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return ReceivedMessageViewHolder(
                ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TYPE_SENT -> {
                (holder as SentMessageViewHolder).setData(chatMessages[position])
            }
            VIEW_TYPE_RECEIVED -> {
                (holder as ReceivedMessageViewHolder).setData(chatMessages[position], profile!!)
            }
        }
    }

    override fun getItemCount(): Int = chatMessages.size

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].sendId == senderId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }
}