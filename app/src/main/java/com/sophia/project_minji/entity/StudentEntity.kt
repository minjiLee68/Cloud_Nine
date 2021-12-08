package com.sophia.project_minji.entity

import androidx.annotation.NonNull
import com.google.firebase.firestore.Exclude
import java.util.*

data class StudentEntity(
    var name: String = "",
    var birth: String = "",
    var phNumber: String = "",
    var image: String = "",
    var character: String = "",
    var user: String = "",
    @Exclude
    var id: String = ""
) {
    fun withId(@NonNull id: String): StudentEntity {
        this.id = id
        return this
    }
}
