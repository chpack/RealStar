package com.example.realstar

import android.content.Context
import androidx.preference.PreferenceManager
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SkyAttr {
    var length = 200
    var size = 100
        set(value) {
            field = value
            sizeChange()
        }
    var sizeChange: () -> Unit = {}

    var num = 6

    val cwidth
        get() = 360 / num

    lateinit var actions: ActionManager

    var moveDuring = 150

    var nx = 100
    var ny = 100

    fun load(context: Context) = PreferenceManager.getDefaultSharedPreferences(context).apply {
        length = getInt("icon_length", 200)
        size = getInt("icon_size", 100)
        moveDuring = getInt("move_during", 150)
        nx = getInt("window_x", 100)
        ny = getInt("window_y", 100)
        dx = IntArray(num) { (sin(it * PI / 3) * length).toInt() }
        dy = IntArray(num) { -(cos(it * PI / 3) * length).toInt() }
    }

    val cw get() = -size / 2
    var dx = IntArray(num) { (sin(it * PI / 3) * length).toInt() }
    var dy = IntArray(num) { -(cos(it * PI / 3) * length).toInt() }
}
