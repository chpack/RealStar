package com.example.realstar

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager

class SkyBack : Service() {
    private lateinit var sky: Sky
    val nm by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreate() {
        super.onCreate()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        sa = (application as SkyApp).sa
        sa.actions = ActionManager(this)
        sky = Sky(this, wm)

        nm.createNotificationChannel(
            NotificationChannel(
                "Controller",
                "Controller",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Simple control Sky Launcher"
            }
        )
        nm.cancelAll()

        val settingPi: PendingIntent = Intent(this, SettingsActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        val appListPI: PendingIntent = Intent(this, AppListActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        val hidePI: PendingIntent = Intent(this, SkyBack::class.java).let {
            it.putExtra("action", getString(R.string.notification_hide))
            PendingIntent.getService(this, 0, it, 0)
        }

        val exitPI: PendingIntent = Intent(this, SkyBack::class.java).let {
            it.putExtra("action", getString(R.string.notification_stop))
            PendingIntent.getService(this, 1, it, 0)
        }

        val notification = Notification.Builder(this, "Controller")
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_info))
            .setSmallIcon(R.drawable.ic_launcher_cube)
            .setLargeIcon(Icon.createWithResource(this, R.drawable.ic_launcher_cube))
            .setContentIntent(hidePI)
            .setTicker("Keep alive")
            .addAction(Notification.Action.Builder(null, getString(R.string.notification_app_list), appListPI).build())
            .addAction(Notification.Action.Builder(null, getString(R.string.notification_setting), settingPi).build())
            .addAction(Notification.Action.Builder(null, getString(R.string.notification_exit), exitPI).build())
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.extras?.let {
            when (it["action"]) {
                "stop" -> {
                    nm.cancelAll()
                    stopForeground(true)
                    stopSelf()
                }
                "hide" -> sky.hide()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
