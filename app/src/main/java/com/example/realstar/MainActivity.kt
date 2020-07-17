package com.example.realstar

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var sky: Sky
    var ss = arrayOf("asdasdf", "ffff", "qwerqwerqewr")


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

        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        var apps = packageManager.queryIntentActivities(mainIntent, 0)


        var lm = LinearLayoutManager(this)
        lists.layoutManager = lm

        var listAdapter = AppListAdapter(ss)
        lists.adapter = listAdapter
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

class ViewH(var view: TextView) : RecyclerView.ViewHolder(view) {

}

class AppListAdapter(var apps: Array<String>) : RecyclerView.Adapter<ViewH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewH {
        val t = TextView(parent.context)
        return ViewH(t)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onBindViewHolder(holder: ViewH, position: Int) {
        holder.view.text = apps[position]
    }


}
