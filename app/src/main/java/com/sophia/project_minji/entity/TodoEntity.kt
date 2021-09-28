package com.sophia.project_minji.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "year")
    var year: Int,
    @ColumnInfo(name = "month")
    var month: Int,
    @ColumnInfo(name = "day")
    var day: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
