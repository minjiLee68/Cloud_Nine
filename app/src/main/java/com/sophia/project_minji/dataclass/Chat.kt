package com.sophia.project_minji.dataclass

import java.util.*

data class Chat(
    var message: String = "",
    var time: String = "",
    var sendId: String = "",
    var receiverId: String = "",
    var userProfile: String = ""
)
