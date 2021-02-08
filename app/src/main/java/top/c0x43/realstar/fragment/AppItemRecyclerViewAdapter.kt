package top.c0x43.realstar.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.realstar.R
import kotlinx.android.synthetic.main.fragment_app_item.view.*
import top.c0x43.realstar.EndAction
import top.c0x43.realstar.sa

class AppItemRecyclerViewAdapter : RecyclerView.Adapter<AppItemRecyclerViewAdapter.ViewHolder>() {

    var type = EndAction.Type.APP

    private var lists = HashMap<EndAction.Type, List<EndAction>>().apply {
        EndAction.Type.values()
            .forEach { type -> put(type, sa.actions.actions.filter { it.type == type }) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.reset(lists[type]!![position])

    override fun getItemCount(): Int = lists[type]!!.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val icon = view.app_item_icon
        private val name = view.app_item_name
        private val packagename = view.app_item_package
        private val status = view.app_item_status

        private lateinit var action: EndAction

        init {
            view.app_item_status.setOnClickListener {
                if (action.line.isEmpty()) {
                    sa.actions.readToAssign = action
                } else {
                    sa.actions.delete(action)
                }
            }
            icon.setOnClickListener {
                sa.actions.launchApp(action)
            }
        }

        fun reset(action: EndAction) {
            this.action = action
            icon.setImageDrawable(action.drawable)
            name.text = action.title
            packagename.text = action.name.removePrefix(action.pack)

            status.setImageResource(
                if (action.line.isEmpty()) android.R.drawable.ic_input_add
                else android.R.drawable.ic_delete
            )
        }


    }
}