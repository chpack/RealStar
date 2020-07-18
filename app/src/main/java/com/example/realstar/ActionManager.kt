package com.example.realstar

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.File
import java.io.FileNotFoundException

class ActionManager(var context: Context) {
    var actions = MutableList<EndAction>(0) { EndAction() }
    var actSet = HashSet<EndAction>()
    var actMap = HashMap<String, EndAction>()

    val size
        get() = actions.size

    var readToAssign: EndAction? = null

    private val filePath =
        context.getExternalFilesDir(null)?.absolutePath + File.separator + "lines.txt"

    init {
        load()
        val mainIntent = Intent(Intent.ACTION_MAIN)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = context.packageManager.queryIntentActivities(mainIntent, 0)
        apps.forEach { r ->
            if (r != null) {
                val ea = EndAction(r, context.packageManager)
                add(ea)
            }
        }
    }

    fun save() {
        var res = ""
        actMap.forEach { (_, u) -> res += "\n$u" }
        File(filePath).writeText(res)
    }

    fun load() {
        try {
            Log.d("asdfasdf",filePath)
            File(filePath).readText().split("\n")
                .forEach { if (it.isNotEmpty()) add(EndAction(it)) }
        } catch (e: FileNotFoundException) {
            save()
            Log.d("asdfasdf", "filenot found")
        }
    }

    private fun add(ea: EndAction) {
        if (actSet.contains(ea)) return

        if (ea.line.isNotEmpty())
            actMap[ea.line] = ea
        actSet.add(ea)
        actions.add(ea)

        if (ea.drawable == null)
            ea.drawable = context.packageManager.getApplicationInfo(ea.pack, 0)
                .loadIcon(context.packageManager)
    }

    fun assign(line: String) {
        if (readToAssign != null) {
            readToAssign!!.line = line
            actMap[line] = readToAssign!!
            readToAssign = null
        }
    }

    fun launchApp(action: EndAction) {
        val intent = Intent()
        intent.component = ComponentName(action.pack, action.name)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    fun launchApp(line:String) {
        val a = get(line)
        if(a != null) launchApp(a)
    }


    operator fun get(line: String): EndAction? = actMap[line]
    operator fun get(ind: Int): EndAction = actions[ind]

    fun delete(action: EndAction) {
        actMap.remove(action.line)
        action.line = ""
    }
}