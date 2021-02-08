package top.c0x43.realstar

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException

@SuppressLint("QueryPermissionsNeeded")
class ActionManager(var context: Context) {
    var actions = MutableList<EndAction>(0) { EndAction() }
    var actSet = HashSet<EndAction>()
    var actMap = HashMap<String, EndAction>()

    var readToAssign: EndAction? = null

    private val filePath =
        context.getExternalFilesDir(null)?.absolutePath + File.separator + "lines.txt"

    init {
        load()
        context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }, 0
        ).forEach {
            it?.let {
                add(EndAction(it, context.packageManager))
            }
        }

        context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_DEFAULT) }, 0
        ).forEach {
            it?.let {
                add(
                    EndAction(it, context.packageManager)
                        .apply { type = EndAction.Type.ACT })
            }
        }
    }

    fun save() {
        var res = ""
        actMap.forEach { (_, u) -> res += "\n$u" }
        File(filePath).writeText(res)
    }

    fun load() = try {
        File(filePath).readText().split("\n")
            .forEach { if (it.isNotEmpty()) add(EndAction(it)) }
    } catch (e: FileNotFoundException) {
        save()
    }

    private fun add(ea: EndAction) {
        if (ea in actSet) return

        if (ea.line.isNotEmpty())
            actMap[ea.line] = ea
        actSet.add(ea)
        actions.add(ea)

        if (ea.drawable == null)
            ea.drawable = context.packageManager.getApplicationInfo(ea.pack, 0)
                .loadIcon(context.packageManager)
    }

    fun assign(line: String) = readToAssign?.let {
        delete(line)
        it.line = line
        actMap[line] = it
        save()
        readToAssign = null
    }

    fun launchApp(action: EndAction) = try {
        context.startActivity(Intent().apply {
            component = ComponentName(action.pack, action.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    } catch (e: Exception) {
        Toast.makeText(context, "Can't launch activity ${e.message}", Toast.LENGTH_LONG).show()
    }

    fun launchApp(line: String) = get(line)?.let { launchApp(it) }

    operator fun get(line: String): EndAction? = actMap[line]
    operator fun get(ind: Int): EndAction = actions[ind]

    fun delete(action: EndAction) {
        actMap.remove(action.line)
        action.line = ""
    }

    fun delete(line: String) = actMap[line]?.let {
        actMap.remove(line)
        it.line = ""
    }
}