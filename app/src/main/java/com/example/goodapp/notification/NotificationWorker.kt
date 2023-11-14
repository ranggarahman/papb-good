package com.example.goodapp.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.goodapp.R
import com.example.goodapp.data.Task
import com.example.goodapp.data.TaskRepository
import com.example.goodapp.ui.detail.DetailTaskActivity
import com.example.goodapp.utils.NOTIFICATION_CHANNEL_ID
import com.example.goodapp.utils.TASK_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val channelName = inputData.getString(NOTIFICATION_CHANNEL_ID)

    private fun getPendingIntent(task: Task): PendingIntent? {
        val intent = Intent(applicationContext, DetailTaskActivity::class.java).apply {
            putExtra(TASK_ID, task.id)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val repository = TaskRepository.getInstance(applicationContext)
        val nearestActiveTask = repository.getNearestActiveTask()

        Log.d(TAG, "ACTIVE TASK NEAR : $nearestActiveTask")
        Log.d(TAG, "CHANNEL NAME : $channelName")

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(nearestActiveTask.title)
            .setContentText(nearestActiveTask.description)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentIntent(getPendingIntent(nearestActiveTask))
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(123, notification)

        return Result.success()
    }

    companion object {
        private const val TAG = "NotificationWorker"
    }
}