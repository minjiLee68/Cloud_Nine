package com.sophia.project_minji.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sophia.project_minji.entity.TodoEntity

@Dao
interface TodoDao {
    // 날짜 정보를 입력 받아서 그 날짜에 해당하는 메모만 반환
    @Query("SELECT * FROM todo WHERE year = :year AND month = :month AND day = :day ORDER BY id DESC")
    fun readDateData(year: Int, month: Int, day: Int): LiveData<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(todo: TodoEntity)

    @Delete
    fun delete(todo: TodoEntity)

    @Update
    fun update(todo: TodoEntity)
}