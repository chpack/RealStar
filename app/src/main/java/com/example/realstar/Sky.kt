package com.example.realstar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat.RGBA_8888
import android.graphics.drawable.Drawable
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.sky_layout.view.*
import kotlin.math.atan2
import kotlin.math.pow


@SuppressLint("ClickableViewAccessibility")
class Sky(context: Context, private var wm: WindowManager) {

    /**
     * Views
     * @root is the root layout, include all other views
     * @stars is the all stars, each one is image view, to show icons
     * @pointer is the touch position
     */
    private var root: ConstraintLayout =
        ConstraintLayout.inflate(context, R.layout.sky_layout, null) as ConstraintLayout
    private var stars: Array<ImageView> = arrayOf(
        root.star_0, root.star_1, root.star_2, root.star_3, root.star_4, root.star_5, root.star_6
    )

    private var subStars = IntArray(6)
    private fun subs(i: Int) = stars[subStars[i]]

    var vib = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    /**
     * UserMode mean operation mode.
     * @activity is mean normal launcher
     * @float is use float window
     */
    enum class UserMode { ACTIVITY, FLOAT }

    private var usermode = UserMode.FLOAT
    private var wlp = WindowManager.LayoutParams()
    private var moveMode = false
    private var sp = PreferenceManager.getDefaultSharedPreferences(context).edit()

    /**
     * Layout of all sectuin
     * @startLP is the first layout
     */
    var layout = ConstraintSet()

    private var xAnim = Array(sa.num + 1) { SpringAnimation(stars[it], SpringAnimation.X) }
    private var yAnim = Array(sa.num + 1) { SpringAnimation(stars[it], SpringAnimation.Y) }

    var xLast = 0f
    var yLast = 0f

    private var path = ""

    private val rootListener = { v: View?, event: MotionEvent? ->
        when {
            v == null || event == null -> false

            event.action == MotionEvent.ACTION_UP && event.eventTime - event.downTime < sa.moveDuring -> {
                moveMode = true
                actionListener(MotionEvent.ACTION_CANCEL, event.rawX, event.rawY)
            }

            moveMode && event.action != MotionEvent.ACTION_UP ->
                setWindow(event.rawX, event.rawY)

            moveMode && event.action == MotionEvent.ACTION_UP -> {
                moveMode = false
                true
            }

            else -> {
                actionListener(event.action, event.rawX, event.rawY)
            }
        }
    }

    init {
        sa.sizeChange = { setSize() }
        sa.load(context)

        initWindow()
        initLPs()
        root.setOnTouchListener(rootListener)
    }


    /**
     * init LayoutParams
     */
    private fun initLPs() {
        layout.clone(root)
        setSize()
    }

    val actionListener: (action: Int, x: Float, y: Float) -> Boolean = { action, x, y ->
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                startSkyLine(x, y)
                startWindow()
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
        var subi = 0
        stars.forEachIndexed { i, v ->
            if (c == v) {
                xAnim[i].animateToFinalPosition(x + sa.cw)
                yAnim[i].animateToFinalPosition(y + sa.cw)
            } else {
                v.setDraw(sa.actions[path + "$subi"]?.drawable)
                xAnim[i].animateToFinalPosition(x + sa.dx[subi] + sa.cw)
                yAnim[i].animateToFinalPosition(y + sa.dy[subi] + sa.cw)
                subStars[subi++] = i
            }
        }
        xLast = x
        yLast = y
    }

    private fun initWindow() {
        wlp.apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            format = RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                    0
            gravity = Gravity.START or Gravity.TOP
            width = sa.size
            height = sa.size
            x = sa.nx
            y = sa.ny
        }
        wm.addView(root, wlp)

    }

    private fun startWindow() {
        if (usermode == UserMode.ACTIVITY) return
        wlp.switchMode(WindowMode.FULL)
    }

    private fun startSkyLine(x: Float, y: Float) {
        path = ""
        xLast = x
        yLast = y
        xAnim.forEach { it.setStartValue(x + sa.cw) }
        yAnim.forEach { it.setStartValue(y + sa.cw) }
        setCenter(x, y)
    }

    private fun linkStars(x: Float, y: Float) {
        val dx = x - xLast
        val dy = y - yLast


        val ang = (Math.toDegrees(atan2(dy * 1.0, dx * 1.0)) + 360 + 90) % 360
        val ind = (ang + sa.cwidth / 2).toInt() % 360 / (360 / sa.num)
        val dis = (dx * dx * 1.0 + dy * dy).pow(0.5)

        if (sa.length - sa.size / 2 < dis && dis < sa.length + sa.size / 2) {
            path += "$ind"
            setCenter(xLast + sa.dx[ind], yLast + sa.dy[ind], subs(ind))
            if (vib.hasVibrator())
                vib.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            else
                Log.d("asfasdf", "no vibra")
        }
    }

    private fun doAction() {
        if (path.isEmpty()) return

        sa.actions.apply {
            if (readToAssign != null) assign(path)
            else launchApp(path)
        }
    }

    private fun endWindow() {
        stars[0].setDraw(sa.actions[""]?.drawable)
        xAnim[0].setStartValue(sa.size / 2f)
        yAnim[0].setStartValue(sa.size / 2f)
        setCenter(sa.size / 2f, sa.size / 2f)
        wlp.switchMode(WindowMode.COMPACT)
    }

    private fun endSkyLine() {}

    private fun setWindow(x: Float, y: Float): Boolean {
        sa.nx = x.toInt() - sa.size / 2
        sa.ny = y.toInt() - sa.size / 2
        sp.putInt("window_x", sa.nx)
        sp.putInt("window_y", sa.ny)
        sp.commit()
        endWindow()
        return true
    }

    private fun setSize() {
        stars.forEach {
            layout.constrainWidth(it.id, sa.size)
            layout.constrainHeight(it.id, sa.size)
        }
        layout.applyTo(root)
    }

    private fun ImageView.setDraw(drawable: Drawable?) =
        if (drawable != null) setImageDrawable(drawable)
        else setImageResource(R.drawable.ic_launcher_cube)

    enum class WindowMode { COMPACT, HIDE, FULL }

    private var windowMode = WindowMode.COMPACT

    private fun WindowManager.LayoutParams.switchMode(m: WindowMode) {
        windowMode = m
        width = when (m) {
            WindowMode.COMPACT -> sa.size
            WindowMode.HIDE -> 0
            WindowMode.FULL -> WindowManager.LayoutParams.MATCH_PARENT
        }
        height = width
        x = sa.nx
        y = sa.ny
        wm.updateViewLayout(root, this)
    }

    fun hide() =
        if (windowMode == WindowMode.HIDE) wlp.switchMode(WindowMode.COMPACT)
        else wlp.switchMode(WindowMode.HIDE)
}