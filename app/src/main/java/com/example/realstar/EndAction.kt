package com.example.realstar

import android.graphics.drawable.Drawable

data class EndAction(var type: Type = Type.APP) {
    enum class Type { APP, MTASK }

    var args = arrayOf("", "")
    val name get() = args[0];
    val pack get() = args[1];

    var drawable: Drawable? = null
}