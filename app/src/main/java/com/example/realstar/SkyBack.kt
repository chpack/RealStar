package com.example.realstar

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager

class SkyBack : Service() {
    lateinit var sky: Sky
    override fun onCreate() {
        super.onCreate()
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        sa = (application as SkyApp).sa
        sky = Sky(this, wm)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
