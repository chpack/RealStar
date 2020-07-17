package com.example.realstar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat.RGBA_8888
import android.transition.TransitionManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin


@SuppressLint("ClickableViewAccessibility")
class Sky(context: Context, private var wm: WindowManager) {

    /**
     * Views
     * @root is the root layout, include all other views
     * @stars is the all stars, each one is image view, to show icons
     * @pointer is the touch position
     */
    var root: ConstraintLayout =
        ConstraintLayout.inflate(context, R.layout.sky_layout, null) as ConstraintLayout
    private var stars: Array<ImageView> = arrayOf(
        root.findViewById(R.id.star_0),
        root.findViewById(R.id.star_1),
        root.findViewById(R.id.star_2),
        root.findViewById(R.id.star_3),
        root.findViewById(R.id.star_4),
        root.findViewById(R.id.star_5),
        root.findViewById(R.id.star_6)
    )
    private var pointer: View = root.findViewById<View>(R.id.pointer)
    private var subStars = IntArray(6)
    private fun subs(i: Int) = stars[subStars[i]]

    /**
     * UserMode mean operation mode.
     * @activity is mean normal launcher
     * @float is use float window
     */
    enum class UserMode { ACTIVITY, FLOAT }

    private var usermode = UserMode.FLOAT
    private var wlp = WindowManager.LayoutParams()
    private var nx = 100
    private var ny = 100

    /**
     * Layout of all sectuin
     * @startLP is the first layout
     */
    private var indexLP = 0
    private var swapLP = arrayOf(ConstraintSet(), ConstraintSet(), ConstraintSet())

    private val startLP: ConstraintSet get() = swapLP[0]
    private val currLP: ConstraintSet get() = swapLP[1 + indexLP % 2]
    private val nextLP: ConstraintSet get() = swapLP[1 + (indexLP + 1) % 2]


    init {
        initWindow()
        initLPs()
        root.setOnTouchListener { v, event ->
            if (v == null || event == null) false
            else rootListener(event.action, event.rawX, event.rawY)
        }

        SkyAttr.sizeChange = { setSize() }
    }

    /**
     * init LayoutParams
     */
    private fun initLPs() {
        swapLP.forEach { cs -> cs.clone(root) }

        startLP.constrainCircle(stars[0].id, pointer.id, 0, 0f)
        stars.forEach { v ->
            if (v != stars[0])
                startLP.constrainCircle(v.id, stars[0].id, 0, 0f)
        }

        setSize()
    }

    val rootListener: (action: Int, x: Float, y: Float) -> Boolean = { action, x, y ->
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                startWindow()
                startSkyLine(x, y)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                linkStars(x, y)
                true
            }
            MotionEvent.ACTION_UP -> {
                doAction()
                endWindow()
                endSkyLine()
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                endWindow()
                endSkyLine()
                true
            }
            else -> false
        }
    }

    private fun setCenter(x: Float, y: Float, c: ImageView = stars[0]) {
        nextLP.setMargin(pointer.id, ConstraintSet.LEFT, x.toInt())
        nextLP.setMargin(pointer.id, ConstraintSet.TOP, y.toInt())
        nextLP.constrainCircle(c.id, pointer.id, 0, 0f)
        var subi = 0
        stars.forEachIndexed { i, v ->
            if (c != v) {
                nextLP.constrainCircle(v.id, c.id, SkyAttr.length, subi * 60f)
                subStars[subi++] = i
            }
        }
        TransitionManager.beginDelayedTransition(root)
        indexLP++
        currLP.applyTo(root)
    }

    private fun initWindow() {
        wlp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        wlp.format = RGBA_8888
        wlp.flags =
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                    0
//        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT
        wlp.x = nx
        wlp.y = ny
        wm.addView(root, wlp)

    }

    private fun startWindow() {
        if (usermode == UserMode.ACTIVITY) return
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT
        wlp.x = nx
        wlp.y = ny
        wm.updateViewLayout(root, wlp)
    }

    private fun startSkyLine(x: Float, y: Float) {
        startLP.setMargin(pointer.id, ConstraintSet.LEFT, x.toInt())
        startLP.setMargin(pointer.id, ConstraintSet.TOP, y.toInt())
        startLP.applyTo(root)
        setCenter(x, y)
    }

    private fun linkStars(x: Float, y: Float) {
        val dx = x - pointer.x
        val dy = y - pointer.y
        val ang = (Math.toDegrees(atan2(dy * 1.0, dx * 1.0)) + 360 + 90) % 360
        val ind = (ang + SkyAttr.cwidth).toInt() % 360 / (360 / SkyAttr.num)
        val dis = (dx * dx * 1.0 + dy * dy).pow(0.5)
        Log.d("asdfasdf", "angle $ang $dis  po ${pointer.x} ${pointer.y}")
        if (SkyAttr.length - SkyAttr.size / 2 < dis && dis < SkyAttr.length + SkyAttr.size / 2)
            setCenter(
                pointer.x + SkyAttr.length * sin(Math.toRadians(ind * 60.0)).toFloat(),
                pointer.y - SkyAttr.length * cos(Math.toRadians(ind * 60.0)).toFloat(),
                subs(ind)
            )
    }

    private fun doAction() {}

    private fun endWindow() {
        wlp.width = SkyAttr.size
        wlp.height = SkyAttr.size
        wlp.x = nx
        wlp.y = ny
        wm.updateViewLayout(root, wlp)
    }

    private fun endSkyLine() {}

    private fun setSize() {
        stars.forEach { v ->
            swapLP.forEach { lp ->
                lp.constrainWidth(v.id, SkyAttr.size)
                lp.constrainHeight(v.id, SkyAttr.size)
            }
        }
    }
}