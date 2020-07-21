package com.example.realstar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat.RGBA_8888
import android.graphics.drawable.Drawable
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.doOnEnd
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.sky_layout.view.*
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
    private var root: ConstraintLayout =
        ConstraintLayout.inflate(context, R.layout.sky_layout, null) as ConstraintLayout
    private var stars: Array<ImageView> = arrayOf(
        root.star_0, root.star_1, root.star_2, root.star_3, root.star_4, root.star_5, root.star_6
    )
    private var pointer: View = root.pointer
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
    private var moveMode = false
    private var sp = PreferenceManager.getDefaultSharedPreferences(context).edit()

    /**
     * Layout of all sectuin
     * @startLP is the first layout
     */
    private var indexLP = 0
    private var swapLP = arrayOf(ConstraintSet(), ConstraintSet(), ConstraintSet())

    private val startLP: ConstraintSet get() = swapLP[0]
    private val currLP: ConstraintSet get() = swapLP[1 + indexLP % 2]
    private val nextLP: ConstraintSet get() = swapLP[1 + (indexLP + 1) % 2]

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
        swapLP.forEach { cs -> cs.clone(root) }

        startLP.constrainCircle(stars[0].id, pointer.id, 0, 0f)
        stars.forEach { v ->
            if (v != stars[0])
                startLP.constrainCircle(v.id, stars[0].id, 0, 0f)
        }

        setSize()
    }

    val actionListener: (action: Int, x: Float, y: Float) -> Boolean = { action, x, y ->
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
        nextLP.apply {
            setMargin(pointer.id, ConstraintSet.LEFT, x.toInt())
            setMargin(pointer.id, ConstraintSet.TOP, y.toInt())
            constrainCircle(c.id, pointer.id, 0, 0f)
        }
        var subi = 0
        stars.forEachIndexed { i, v ->
            if (c == v) return@forEachIndexed
            v.setDraw(sa.actions[path + "$subi"]?.drawable)
            nextLP.constrainCircle(v.id, c.id, sa.length, subi * 60f)
            subStars[subi++] = i
        }
        val t = AutoTransition().apply { duration = 100 }
        TransitionManager.beginDelayedTransition(root, t)
        indexLP++
        currLP.applyTo(root)
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
        wlp.switchMode(false)
    }

    private fun startSkyLine(x: Float, y: Float) {
        path = ""
        startLP.setMargin(pointer.id, ConstraintSet.TOP, y.toInt())
        startLP.setMargin(pointer.id, ConstraintSet.LEFT, x.toInt())
        TransitionManager.beginDelayedTransition(root, AutoTransition().apply {
            duration = 0
            doOnEnd { setCenter(x, y) }
        })
        startLP.applyTo(root)
    }

    private fun linkStars(x: Float, y: Float) {
        val dx = x - pointer.x
        val dy = y - pointer.y

        val ang = (Math.toDegrees(atan2(dy * 1.0, dx * 1.0)) + 360 + 90) % 360
        val ind = (ang + sa.cwidth / 2).toInt() % 360 / (360 / sa.num)
        val dis = (dx * dx * 1.0 + dy * dy).pow(0.5)

        if (sa.length - sa.size / 2 < dis && dis < sa.length + sa.size / 2) {
            path += "$ind"
            setCenter(
                pointer.x + sa.length * sin(Math.toRadians(ind * 60.0)).toFloat(),
                pointer.y - sa.length * cos(Math.toRadians(ind * 60.0)).toFloat(),
                subs(ind)
            )
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
        setCenter(sa.size / 2f, sa.size / 2f)
        wlp.switchMode(true)
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
        stars.forEach { v ->
            swapLP.forEach { lp ->
                lp.constrainWidth(v.id, sa.size)
                lp.constrainHeight(v.id, sa.size)
            }
        }
    }

    private fun ImageView.setDraw(drawable: Drawable?) =
        if (drawable != null) setImageDrawable(drawable)
        else setImageResource(R.drawable.ic_launcher_background)

    private fun WindowManager.LayoutParams.switchMode(m: Boolean) {
        width = if (m) sa.size else WindowManager.LayoutParams.MATCH_PARENT
        height = if (m) sa.size else WindowManager.LayoutParams.MATCH_PARENT
        x = sa.nx
        y = sa.ny
        wm.updateViewLayout(root, this)
    }
}