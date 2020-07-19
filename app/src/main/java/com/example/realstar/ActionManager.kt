package com.example.realstar

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException

class ActionManager(var context: Context) {
    var actions = MutableList<EndAction>(0) { EndAction() }
    var actSet = HashSet<EndAction>()
    var actMap = HashMap<String, EndAction>()

    var readToAssign: EndAction? = null

    private val filePath =
        context.getExternalFilesDir(null)?.absolutePath + File.separator + "lines.txt"

    init {
        load()
        var mainIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        var apps = context.packageManager.queryIntentActivities(mainIntent, 0)
        apps.forEach { r ->
            if (r != null) {
                val ea = EndAction(r, context.packageManager)
                add(ea)
            }
        }
        mainIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_DEFAULT) }
        apps = context.packageManager.queryIntentActivities(mainIntent, 0)
        apps.forEach { r ->
            if (r != null) {
                val ea = EndAction(r, context.packageManager)
                ea.type = EndAction.Type.ACT
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
            File(filePath).readText().split("\n")
                .forEach { if (it.isNotEmpty()) add(EndAction(it)) }
        } catch (e: FileNotFoundException) {
            save()
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
        if (readToAssign == null) return
        delete(line)
        readToAssign!!.line = line
        actMap[line] = readToAssign!!
        save()
        readToAssign = null
    }

    fun launchApp(action: EndAction) {
        val intent = Intent()
        intent.component = ComponentName(action.pack, action.name)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Can't launch activity ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun launchApp(line: String) {
        val a = get(line)
        if (a != null) launchApp(a)
    }


    operator fun get(line: String): EndAction? = actMap[line]
    operator fun get(ind: Int): EndAction = actions[ind]

    fun delete(action: EndAction) {
        actMap.remove(action.line)
        action.line = ""
    }

    fun delete(line: String) {
        val ae = actMap[line]
        if (ae != null) {
            ae.line = ""
            actMap.remove(line)
        }
    }
}