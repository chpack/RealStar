package com.example.realstar

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

data class EndAction(
    var title: String = "",
    var args: Array<String> = arrayOf("", ""),
    var drawable: Drawable? = null,
    var line: String = "",
    var type: Type = Type.APP
) {
    enum class Type { APP, MTASK }

    val name get() = args[0]
    val pack get() = args[1]
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EndAction

        if (!args.contentEquals(other.args)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = args.contentHashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String = "|$title|$name|$pack|$line|${type.name}|"

    constructor(s: String) : this() {
        val ss = s.split('|')
        title = ss[0]
        args[0] = ss[1]
        args[1] = ss[2]
        line = ss[3]
        type = Type.valueOf(ss[4])
    }

    constructor(r: ResolveInfo, pm: PackageManager) : this() {
        title = r.loadLabel(pm).toString()
        args[0] = r.activityInfo.name ?: ""
        args[1] = r.activityInfo.packageName?: ""
        type = Type.APP
        drawable = r.loadIcon(pm)
    }

}