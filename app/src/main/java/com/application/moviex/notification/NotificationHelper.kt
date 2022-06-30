package com.application.moviex.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.application.moviex.MovieDetailsActivity
import com.application.moviex.R

class NotificationHelper(var context: Context, var msg: String) {
    private val CHANNEL_ID = "Moviex"
    private val NOTIFICATION_ID = 123

    fun notification() {
        createNotificationChannel()

        val sendInt = Intent(context, MovieDetailsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivities(context, 0, arrayOf(sendInt), 0)
        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_notification)
        val isNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(icon)
            .setContentTitle("Movie is Playing")
            .setContentText(msg)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context)
            .notify(NOTIFICATION_ID, isNotification)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val desc = "Moviex App"
            val imports = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, imports).apply {
                description = desc
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}