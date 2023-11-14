package com.example.goodapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.goodapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors

//Define room database class and prepopulate database using JSON
@Database(entities = [Task::class], version = 2)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                Executors.newSingleThreadExecutor().execute {
                                    fillWithStartingData(context, database.taskDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun fillWithStartingData(context: Context, dao: TaskDao) {
            val tasksJsonArray = loadJsonArray(context)
            try {
                if (tasksJsonArray != null) {
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        for (i in 0 until tasksJsonArray.length()) {
                            val taskJsonObject = tasksJsonArray.getJSONObject(i)
                            val task = Task(
                                taskJsonObject.getInt("id"),
                                taskJsonObject.getString("title"),
                                taskJsonObject.getString("description"),
                                taskJsonObject.getLong("dueDate"),
                                taskJsonObject.getBoolean("completed")
                            )
                            dao.insertTask(task)
                        }
                    }
                }
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }

        private fun loadJsonArray(context: Context): JSONArray? {
            val builder = StringBuilder()
            val inputStream = context.resources.openRawResource(R.raw.task)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }
                val json = JSONObject(builder.toString())
                return json.getJSONArray("tasks")
            } catch (exception: IOException) {
                exception.printStackTrace()
            } catch (exception: JSONException) {
                exception.printStackTrace()
            } finally {
                reader.close()
            }
            return null
        }
    }
}