package com.example.realstar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.usage.UsageEvents
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.PixelFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.floatlayout.*
import kotlin.jvm.internal.Ref

class MainActivity : AppCompatActivity() {

    //    var wm :
    var but: View? = null

    var startc = ConstraintSet();
    var endc = ConstraintSet();
    var i = 0

    lateinit var sky: Sky

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startc.clone(clay)
        endc.clone(clay)

        startc.constrainCircle(R.id.button4, R.id.dissbut, 100, 0f)
        endc.constrainCircle(R.id.button4, R.id.dissbut, 200, 180f)

        dissbut.setOnClickListener { v ->
            if (v.layoutParams is ConstraintLayout.LayoutParams)
                Log.d("asdfasdf", "is constlp")

            TransitionManager.beginDelayedTransition(clay)
            if (i++ % 2 == 0) {
                startc.applyTo(clay)
                button4.text = "ffff"
            } else {
                endc.applyTo(clay)
                button4.text = "faaaa"
            }
        }
        sky = Sky(this, windowManager)
        but = sky.root

        showbut.setOnClickListener { v ->
            Log.d("asdfasdf", "show click")
            if (Settings.canDrawOverlays(this)) {
//                showFloat();
            } else {
                getPermis();
            }
        }

    }


    fun getPermis() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, 1);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (Settings.canDrawOverlays(this)) {
                Log.d("asdfasdf", "success");
            } else {
                Log.d("asdfasdf", "failed");
            }
        }
    }

}
