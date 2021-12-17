package com.sophia.project_minji.entity

import java.util.HashMap

data class FollowDto(
    var followerCount: Int = 0,
    var followers: MutableMap<String, Boolean> = HashMap(),

    var followingCount: Int = 0,
    var followings: MutableMap<String, Boolean> = HashMap()
)
