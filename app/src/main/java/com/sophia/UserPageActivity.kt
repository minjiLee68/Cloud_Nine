package com.sophia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sophia.project_minji.databinding.ActivityUserPageBinding
import com.sophia.project_minji.entity.Chat
import com.sophia.project_minji.entity.FollowDto
import com.sophia.project_minji.entity.FollowUser
import com.sophia.project_minji.utillties.PreferenceManager

class UserPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPageBinding

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        getFolloerAndFollowing()
        detailUser()
        binding.followBtn.setOnClickListener { requestFollow() }
    }

    private fun init() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        preferenceManager = PreferenceManager(applicationContext)
    }

    private fun detailUser() {
        val userId = intent.getStringExtra("followUserId").toString()
        firestore.collection("Users").document(userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result!!.exists()) {
                        val name = task.result!!.getString("name")
                        val image = task.result!!.getString("image")
                        binding.nickName.text = name
                        Glide.with(applicationContext).load(image).into(binding.myProfile)
                    }
                }
            }
    }

    private fun requestFollow() {
        val userId = preferenceManager.getString("followUserId")!!
        val followingUser = firestore.collection("follow").document(uid)
        firestore.runTransaction {
            var followDto = it.get(followingUser).toObject(FollowDto::class.java)
            if (followDto == null) {
                followDto = FollowDto()
                followDto.followingCount = 1
                followDto.followings[userId] = true
                it.set(followingUser, followDto)

                return@runTransaction
            } else {
                with(followDto) {
                    if (followings.containsKey(userId)) {
                        //언팔로우
                        followingCount -= 1
                        followings.remove(userId)
                    } else {
                        //팔로우
                        followingCount += 1
                        followings[userId] = true
                        preferenceManager.putString("followingId", userId)
                    }
                }
            }
//            if (followDto.followings.containsKey(userId)) {
//                followDto.followingCount = followDto.followingCount - 1
//                followDto.followers.remove(userId)
//            } else {
//                followDto.followingCount = followDto.followingCount + 1
//                followDto.followings[userId] = true
//            }
            it.set(followingUser, followDto)
            return@runTransaction
        }

        val followerUser = firestore.collection("follow").document(userId)
        firestore.runTransaction {
            var followDto = it.get(followerUser).toObject(FollowDto::class.java)
            if (followDto == null) {
                followDto = FollowDto()
                followDto!!.followerCount = 1
                followDto!!.followers[uid] = true

                it.set(followerUser, followDto!!)
                return@runTransaction
            } else {
                with(followDto) {
                    if (this!!.followers.containsKey(uid)) {
                        //언팔로우
                        followerCount -= 1
                        followers.remove(uid)
                    } else {
                        //팔로우
                        followerCount += 1
                        followers[uid] = true
                    }
                }
            }
//            if (followDto!!.followers.containsKey(uid)) {
//                followDto!!.followerCount = followDto!!.followerCount - 1
//                followDto!!.followers.remove(uid)
//            } else {
//                followDto!!.followerCount = followDto!!.followerCount + 1
//                followDto!!.followers[uid] = true
//            }
            it.set(followerUser, followDto!!)
            return@runTransaction
        }
    }

    private fun getFolloerAndFollowing() {
        val userId = preferenceManager.getString("userId")!!
        firestore.collection("follow").document(userId).addSnapshotListener { value, _ ->
            if (value != null) {
                val followDTO = value.toObject(FollowDto::class.java)

                if (followDTO?.followingCount != null) {
                    binding.following.text = followDTO.followingCount.toString()
                }
                if (followDTO?.followerCount != null) {
                    binding.follower.text = followDTO.followerCount.toString()
                }
            }
        }
    }

}