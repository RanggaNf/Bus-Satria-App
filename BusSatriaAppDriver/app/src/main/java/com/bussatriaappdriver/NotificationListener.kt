package com.bussatriaappdriver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*

class NotificationListener : Service() {
    private lateinit var database: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        try {
            Log.d(TAG, "Service created")
            database = FirebaseDatabase.getInstance().reference.child("notifications")
            createNotificationChannel()
            startForeground()
            startListening()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate: ${e.message}", e)
            stopSelf()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Driver Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for Bus Satria Driver"
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startListening() {
        Log.d(TAG, "Start listening for notifications")
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "New notification received: ${snapshot.getValue()}")
                val notification = snapshot.getValue(Notification::class.java)
                notification?.let {
                    Log.d(TAG, "Showing notification: ${it.message}")
                    showNotification(it.message)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "Notification changed: ${snapshot.getValue()}")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "Notification removed: ${snapshot.getValue()}")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "Notification moved: ${snapshot.getValue()}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Database error: ${error.message}")
            }
        })
    }

    private fun showNotification(message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Penumpang Menunggu")
            .setContentText(message)
            .setSmallIcon(R.drawable.busmerah)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "Notification shown with ID: $notificationId and message: $message")
    }

    private fun startForeground() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bus Satria Driver")
            .setContentText("Layanan notifikasi aktif")
            .setSmallIcon(R.drawable.busmerah)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(FOREGROUND_SERVICE_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(FOREGROUND_SERVICE_ID, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "NotificationListener"
        private const val CHANNEL_ID = "driver_channel"
        private const val FOREGROUND_SERVICE_ID = 1001
        private const val NOTIFICATION_ID = 1002
    }
}

data class Notification(
    val message: String = "",
    val timestamp: Long = 0
)