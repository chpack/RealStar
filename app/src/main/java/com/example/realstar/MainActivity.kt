package com.example.realstar

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var sky: Sky

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sky = Sky(this, windowManager)

        showbut.setOnClickListener {
            Log.d("asdfasdf", "show click")
            if (Settings.canDrawOverlays(this)) {
//                showFloat()
            } else {
                getPermis()
            }
        }
    }


    fun getPermis() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (Settings.canDrawOverlays(this)) {
                Log.d("asdfasdf", "success")
            } else {
                Log.d("asdfasdf", "failed")
            }
        }
    }

}
