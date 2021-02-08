package top.c0x43.realstar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.realstar.R

lateinit var sa: SkyAttr

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

