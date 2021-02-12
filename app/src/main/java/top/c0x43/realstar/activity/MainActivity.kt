package top.c0x43.realstar.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.realstar.R
import top.c0x43.realstar.SkyApp
import top.c0x43.realstar.sa
import top.c0x43.realstar.service.SkyBack


class MainActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sa = (application as SkyApp).sa
        startService(Intent(baseContext, SkyBack::class.java))

        startActivity(Intent(baseContext, SettingsActivity::class.java))
    }
}

