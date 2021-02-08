package top.c0x43.realstar.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realstar.R
import kotlinx.android.synthetic.main.fragment_app_item_list.view.*
import top.c0x43.realstar.EndAction
import top.c0x43.realstar.SkyApp
import top.c0x43.realstar.sa

/**
 * A fragment representing a list of Items.
 */
class AppItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_app_item_list, container, false)


//      Set the adapter
        view.app_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AppItemRecyclerViewAdapter()
            sa = (activity?.application as SkyApp).sa
        }

        // Set touch actions
        val adapter = view.app_list.adapter as AppItemRecyclerViewAdapter

        view.show_launcher.setOnClickListener {
            adapter.type = EndAction.Type.APP
            adapter.notifyDataSetChanged()
        }
        view.show_all.setOnClickListener {
            adapter.type = EndAction.Type.ACT
            adapter.notifyDataSetChanged()
        }
        view.showbut.setOnClickListener {
            if (Settings.canDrawOverlays(context)) {
//                showFloat()
            } else {
                getPermis()
            }
        }

        return view
    }

    private fun getPermis() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${context?.packageName}")
        startActivityForResult(intent, 1)
    }
}