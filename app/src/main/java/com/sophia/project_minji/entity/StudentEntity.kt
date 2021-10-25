package com.sophia.project_minji.entity

import java.util.*

data class StudentEntity(
    var name: String? = "",
    var birth: String? = "",
    var phNumber: String? = "",
    var image: String? = "",
    var character: String? = "",
    var date: Date = Date(),
    var id: String? = ""
)
