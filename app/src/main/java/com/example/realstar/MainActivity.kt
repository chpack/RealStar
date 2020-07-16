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
    var gravity = Gravity.CENTER
    var lp = WindowManager.LayoutParams()

    var nx: Int = 100
    var ny: Int = 100

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
//            val l = (v.layoutParams as ConstraintLayout.LayoutParams)
//            l.circleAngle +=10
//            v.layoutParams = l;
        }
//        wm = applicationContext.getSystemService(Context.WINDOW_SERVICE)

//        but = Button(this);
//        (but as Button).text = "a butt";
//        but = ConstraintLayout.inflate(this, R.layout.floatlayout, null)
        sky = Sky(this, windowManager)
        but = sky.root
//        but?.findViewById<Button>(R.id.button)?.setOnClickListener {   v->Log.d("asdfasdf","click") }
//        button.setOnClickListener { v->Log.d("asdfasdf","click") }
//        but?.setOnClickListener { v -> Log.d("asdfasdf", "click") }
//        var vm = WRAP_CONTENT_TOUCHABLE;

        showbut.setOnClickListener { v ->
            Log.d("asdfasdf", "show click")
            if (Settings.canDrawOverlays(this)) {
                showFloat();
            } else {
                getPermis();
            }
        }
//        val li: (View, MotionEvent) -> Boolean = { v, event ->
//            Log.d(
//                "asdfasfd",
//                "x${event.x}  rawx${event.rawX}  xp${event.xPrecision} action ${event.action} ${when (v.id) {
//                    R.id.button -> "but"
//                    R.id.imageView2 -> "img"
//                    R.id.asdf -> "layout"
//                    else -> "other${v.id} ${R.id.button} ${R.id.imageView2}"
//                }
//                }"
//            )
//            when (v.id) {
//                R.id.button -> {
//                    Log.d("asdfasdf", "button on touch")
//
////                    val l = (v.layoutParams as ConstraintLayout.LayoutParams)
////                    l.rightToLeft = l.leftToRight
////                    l.bottomToTop = l.topToBottom
////                    l.leftToRight = 0
////                    l.topToBottom = 0
//                    true
//                }
//                R.id.imageView2 -> {
//
//                    if (event.action == MotionEvent.ACTION_DOWN) {
//                        nx = event.rawX.toInt()
//                        ny = event.rawY.toInt()
//                    }
//                    val dx = event.rawX.toInt() - nx
//                    val dy = event.rawY.toInt() - ny
//                    lp.x += dx
//                    lp.y += dy
////            lp.x += event.x.toInt()
////            lp.y += event.y.toInt()
//                    nx = event.rawX.toInt()
//                    ny = event.rawY.toInt()
//                    update()
//                    true
//                }
//                else -> {
//                    Log.d("asdfasdf", "other")
//                    super.onTouchEvent(event)
////                    false
//                }
//            }
//
//        }

//        but?.setOnTouchListener(li)
//        but?.findViewById<Button>(R.id.button)?.setOnTouchListener(li)
//        but?.findViewById<ImageView>(R.id.imageView2)?.setOnTouchListener(li)

    }

    fun update() {
        windowManager.updateViewLayout(but, lp)
    }

    fun showFloat() {
//        var lp = WindowManager.LayoutParams()
        Log.d("asdfasdf", "start float")
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        lp.format = PixelFormat.RGBA_8888;
        lp.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                    0
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.x = nx
        lp.y = ny
        windowManager.addView(but, lp)
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
