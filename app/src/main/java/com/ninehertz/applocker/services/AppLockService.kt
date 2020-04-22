package com.ninehertz.applocker.services

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat


class AppLockService : Service() {

    lateinit var handler : Handler
    lateinit var context : Context
    lateinit var launchChecker : CheckAppLauncherThread
    private val mBinder: IBinder = MyBinder()


    fun start(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, AppLockService::class.java))
        } else {
            context.startService(Intent(context, AppLockService::class.java))
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val res = super.onStartCommand(intent, flags, startId)
        start(baseContext)
        return START_STICKY
    }

        override fun onCreate() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
             createNotificationChannel()
            }
            else
                startForeground(
                    1,
                    Notification()
                )
        handler = Handler(mainLooper)
        context = applicationContext
        launchChecker = CheckAppLauncherThread(handler, context)
        launchChecker.start()
        super.onCreate()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val NOTIFICATION_CHANNEL_ID = "com.ninehertz.applockpro"
            val channelName = "My Background Service"
            val chan =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_NONE
                )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager =
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(chan)
            val notificationBuilder =
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_lock_lock)
                .setContentTitle("App is running in background")
                .setContentText("Tap to close service")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder;
    }

    class MyBinder : Binder() {
        val service: AppLockService
            get() = AppLockService()
    }
}