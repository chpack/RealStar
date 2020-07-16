package com.example.realstar

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import kotlin.Float

object SkyAttr {
    var length = 100
    var size = 100
}

@SuppressLint("ClickableViewAccessibility")
class Sky(context: Context, wm: WindowManager) {

    var root: ConstraintLayout =
        ConstraintLayout.inflate(context, R.layout.sky_layout, null) as ConstraintLayout
    var stars = arrayOf(
        root.findViewById<ImageView>(R.id.star_0),
        root.findViewById<ImageView>(R.id.star_1),
        root.findViewById<ImageView>(R.id.star_2),
        root.findViewById<ImageView>(R.id.star_3),
        root.findViewById<ImageView>(R.id.star_4),
        root.findViewById<ImageView>(R.id.star_5),
        root.findViewById<ImageView>(R.id.star_6)
    )

    enum class UserMode { ACTIVITY, FLOAT }

    var usermode = UserMode.FLOAT

    var startLP = ConstraintSet()
    var midLPs = arrayOf(ConstraintSet(), ConstraintSet())
    var currLP: ConstraintSet
        get() = midLPs[indexLP % 2]
        set(value) {
            midLPs[indexLP % 2] = value
        }

    var nextLP: ConstraintSet
        get() = midLPs[(indexLP + 1) % 2]
        set(value) {
            midLPs[(indexLP + 1) % 2] = value
        }

    var indexLP = 0;

    var pointer = root.findViewById<View>(R.id.pointer)

    init {
        midLPs[0].clone(root)
        midLPs[1].clone(root)
        startLP.clone(root)
        startLP.constrainCircle(stars[0].id,pointer.id,0,0f)
        stars.forEach { v ->
            if (v != stars[0])
                startLP.constrainCircle(v.id, stars[0].id, 0, 0f)
        }
        startLP.constrainCircle(stars[0].id, pointer.id, 0, 0f)
        root.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pointer.x = event.rawX
                    pointer.y = event.rawY

                    startLP.applyTo(root)
//                    stars[0].x = event.rawX - SkyAttr.size/2
//                    stars[0].y = event.rawY - SkyAttr.size/2
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    pointer.x = event.rawX
                    pointer.y = event.rawY

                    setCenter(stars[0], event.rawX, event.rawY)
                    indexLP++
                    currLP.applyTo(root)
                    true

                }
                MotionEvent.ACTION_UP -> {
                    true
                }
                else -> {
                    false
                }
            }
        }
    }


    fun setCenter(c: ImageView, x: Float, y: Float) {
        setSize()
        SkyAttr.size+=10
        currLP.constrainCircle(c.id, pointer.id, 0, 0f)
        currLP.connect(pointer.id,ConstraintSet.LEFT,root.id, ConstraintSet.LEFT, x.toInt())
        currLP.connect(pointer.id,ConstraintSet.TOP,root.id, ConstraintSet.TOP, y.toInt())
//        Log.d("asdfasdf", "${c.x} ${c.y} ${pointer.x} ${pointer.y}")
        stars.forEachIndexed { i, v ->
            if (c != v)
                nextLP.constrainCircle(v.id, c.id, SkyAttr.length, i * 60f)
        }
    }

    fun setSize(){
        stars.forEach { v->
            currLP.constrainWidth(v.id,SkyAttr.size)
            startLP.constrainWidth(v.id,SkyAttr.size)
            nextLP.constrainWidth(v.id,SkyAttr.size)
            currLP.constrainHeight(v.id,SkyAttr.size)
            startLP.constrainHeight(v.id,SkyAttr.size)
            nextLP.constrainHeight(v.id,SkyAttr.size)
        }
    }
}