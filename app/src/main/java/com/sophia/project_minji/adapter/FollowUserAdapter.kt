package com.sophia.project_minji.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sophia.project_minji.userPage.UserPageActivity
import com.sophia.project_minji.databinding.ItemContainerUserBinding
import com.sophia.project_minji.dataclass.User
import com.sophia.project_minji.utillties.PreferenceManager

class FollowUserAdapter( private val users: ArrayList<User>) : ListAdapter<User, FollowUserAdapter.FollowUserViewHolder>(

    object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.name == newItem.name && oldItem.image == newItem.image

    }

) {
    inner class FollowUserViewHolder(
        private val binding: ItemContainerUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val preferenceManager: PreferenceManager = PreferenceManager(itemView.context)

        fun setUserData(user: User) {
            binding.textName.text = user.name
            Glide.with(itemView.context)
                .load(user.image)
                .into(binding.imageProfile)

            binding.root.setOnClickListener {
                val followUserIntent = Intent(itemView.context, UserPageActivity::class.java)
                followUserIntent.putExtra("followUserId", user.userId)
                itemView.context.startActivity(followUserIntent)
                preferenceManager.putString("nickName", user.name)
                preferenceManager.putString("profile", user.image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowUserViewHolder =
        FollowUserViewHolder(
            ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: FollowUserViewHolder, position: Int) {
        val followUsers = users[position]
        holder.setUserData(followUsers)
    }
}