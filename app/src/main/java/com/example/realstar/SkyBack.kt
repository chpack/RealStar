package com.example.realstar

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import android.view.WindowManager

class SkyBack : Service() {
    private lateinit var sky: Sky
    override fun onCreate() {
        super.onCreate()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        sa = (application as SkyApp).sa
        sa.actions = ActionManager(this)
        sky = Sky(this, wm)

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                "Controller",
                "Controller",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Simple control Sky Launcher"
                importance = NotificationManager.IMPORTANCE_LOW
            }
        )

        val settingPi: PendingIntent = Intent(this, SettingsActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        val appListPI: PendingIntent = Intent(this, AppListActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        val hidePI: PendingIntent = Intent(this, SkyBack::class.java).let {
            it.putExtra("action", "hide")
            PendingIntent.getService(this, 0, it, 0)
        }

        val exitPI: PendingIntent = Intent(this, SkyBack::class.java).let {
            it.putExtra("action", "stop")
            PendingIntent.getService(this, 0, it, 0)
        }

        val notification = Notification.Builder(this, "Controller")
            .setContentTitle("Real Star Launcher")
            .setContentText("Touch to hide")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(Icon.createWithResource(this, R.drawable.ic_launcher_background))
            .setContentIntent(hidePI)
            .setTicker("Keep alive")
            .addAction(Notification.Action.Builder(null, "App List", appListPI).build())
            .addAction(Notification.Action.Builder(null, "Setting", settingPi).build())
            .addAction(Notification.Action.Builder(null, "Exit", exitPI).build())
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.let {
            when (it["action"]) {
                "stop" -> stopSelf()
                "hide" -> sky.hide()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
