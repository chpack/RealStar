package top.c0x43.realstar.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.realstar.R
import top.c0x43.realstar.sa

class SettingsFragment : PreferenceFragmentCompat() {
    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
            if (sp != null) when (key) {
                "icon_size" -> sa.size = sp.getInt(key, sa.size)
                "icon_length" -> sa.length = sp.getInt(key, sa.length)
                "move_during" -> sa.moveDuring = sp.getInt(key, sa.moveDuring)
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

}