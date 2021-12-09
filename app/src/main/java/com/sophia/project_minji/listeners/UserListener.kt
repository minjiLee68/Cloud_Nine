package com.sophia.project_minji.listeners

import com.sophia.project_minji.entity.User


interface UserListener {
    fun onUserClicked(user: User)
}