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
            NotificationChannel("asdf", "asdfasdf", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "test text"
            }
        )

        val pi: PendingIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)

        }
        val notification = Notification.Builder(this, "asdf")
            .setContentTitle("Keep alive")
            .setContentText("Kepp alive")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(Icon.createWithResource(this, R.drawable.ic_launcher_background))
            .setContentIntent(pi)
            .setTicker("Keep alive")
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
