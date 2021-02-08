package top.c0x43.realstar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.realstar.R
import top.c0x43.realstar.fragment.AppItemFragment
import top.c0x43.realstar.fragment.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace( R.id.settings, SettingsFragment() )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}