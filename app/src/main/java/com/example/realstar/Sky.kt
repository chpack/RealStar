package com.example.realstar

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

object SkyAttr {
    var length = 200
    var size = 100
        set(value) {
            field = value
            sizeChange()
        }
    var sizeChange: () -> Unit = {}
}

@SuppressLint("ClickableViewAccessibility")
class Sky(context: Context, wm: WindowManager) {

    /**
     * Views
     * @root is the root layout, include all other views
     * @stars is the all stars, each one is image view, to show icons
     * @pointer is the touch position
     */
    var root: ConstraintLayout =
        ConstraintLayout.inflate(context, R.layout.sky_layout, null) as ConstraintLayout
    var stars: Array<ImageView> = arrayOf(
        root.findViewById<ImageView>(R.id.star_0),
        root.findViewById<ImageView>(R.id.star_1),
        root.findViewById<ImageView>(R.id.star_2),
        root.findViewById<ImageView>(R.id.star_3),
        root.findViewById<ImageView>(R.id.star_4),
        root.findViewById<ImageView>(R.id.star_5),
        root.findViewById<ImageView>(R.id.star_6)
    )
    var pointer: View = root.findViewById<View>(R.id.pointer)

    /**
     * UserMode mean operation mode.
     * @activity is mean normal launcher
     * @float is use float window
     */
    enum class UserMode { ACTIVITY, FLOAT }

    private var usermode = UserMode.FLOAT

    /**
     * Layout of all sectuin
     * @startLP is the first layout
     */
    var indexLP = 0
    private var swapLP = arrayOf(ConstraintSet(), ConstraintSet(), ConstraintSet())

    private val startLP: ConstraintSet get() = swapLP[0]
    private val currLP: ConstraintSet get() = swapLP[1 + indexLP % 2]
    private val nextLP: ConstraintSet get() = swapLP[1 + (indexLP + 1) % 2]


    init {
        initWindow()
        initLPs()
        root.setOnTouchListener { v, event ->
            if (v == null || event == null) false
            else rootListener(v, event)
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
    }

    val rootListener: (v: View, event: MotionEvent) -> Boolean = { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                setCenter(event.rawX, event.rawY)
                startWindow(event)
                startSkyLine(event)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                linkStars(event)
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
        currLP.constrainCircle(c.id, pointer.id, 0, 0f)
        nextLP.setMargin(pointer.id, ConstraintSet.LEFT, x.toInt())
        nextLP.setMargin(pointer.id, ConstraintSet.TOP, y.toInt())
        stars.forEachIndexed { i, v ->
            if (c != v)
                nextLP.constrainCircle(v.id, c.id, SkyAttr.length, i * 60f)
        }
    }

    private fun initWindow() {}


    private fun startWindow(event: MotionEvent) {
        if (usermode == UserMode.ACTIVITY) return
    }

    private fun startSkyLine(event: MotionEvent) {
        startLP.applyTo(root)
    }

    private fun linkStars(event: MotionEvent) {
        setCenter(event.rawX, event.rawY)
        indexLP++
        currLP.applyTo(root)
    }

    private fun doAction() {}

    private fun endWindow() {}

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