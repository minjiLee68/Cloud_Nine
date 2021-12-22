package com.sophia.project_minji.adapter

import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sophia.project_minji.databinding.ItemContainerUserBinding
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.sophia.project_minji.chats.ChatActivity
import com.sophia.project_minji.entity.FollowUser
import com.sophia.project_minji.entity.User
import com.sophia.project_minji.utillties.PreferenceManager
import kotlin.collections.ArrayList

class UsersAdapter( private val users: ArrayList<FollowUser>
) : ListAdapter<FollowUser, UsersAdapter.UserViewHolder>(

    object : DiffUtil.ItemCallback<FollowUser>() {
        override fun areItemsTheSame(oldItem: FollowUser, newItem: FollowUser): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: FollowUser, newItem: FollowUser): Boolean =
            oldItem.name == newItem.name && oldItem.image == newItem.image

    }

) {
    inner class UserViewHolder(
        private val binding: ItemContainerUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val preferenceManager: PreferenceManager = PreferenceManager(itemView.context)

        fun setUserData(user: FollowUser) {
            binding.textName.text = user.name
            Glide.with(itemView.context)
                .load(user.image)
                .into(binding.imageProfile)

            binding.root.setOnClickListener {
                val userAdapter = Intent(itemView.context, ChatActivity::class.java)
                userAdapter.putExtra("userId", user.userId)
                preferenceManager.putString("userId",user.userId)
                itemView.context.startActivity(userAdapter)
                preferenceManager.putString("nickName", user.name)
                preferenceManager.putString("profile", user.image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val userList = users[position]
        holder.setUserData(userList)
    }
}