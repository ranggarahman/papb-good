package com.example.goodapp.data

//Define a local database table using the schema in app/schema/tasks.json
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDateMillis: Long,
    val isCompleted: Boolean = false
)
