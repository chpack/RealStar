package com.example.realstar

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
}
