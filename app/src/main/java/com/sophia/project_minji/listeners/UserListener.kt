package com.sophia.project_minji.listeners

import com.sophia.project_minji.dataclass.User

interface UserListener {
    fun onUserClicked(user: User)
}