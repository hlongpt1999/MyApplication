package com.example.myapplication.feature.hide

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAppBinding
import com.example.myapplication.utilsjava.AppInfoUtil
import com.example.myapplication.utilsjava.PrefMgr

class AppListAdapter(val list: ArrayList<AppInfoUtil.AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = list[position]
        holder.binding.imvIcon.setImageDrawable(item.icon)
        holder.binding.tvAppName.text = item.label
        holder.binding.tvPackageName.text = item.packageName
        val hiddenApps = PrefMgr.getHideApps()
        holder.binding.cbSelect.isChecked = hiddenApps.contains(item.packageName)
        holder.binding.cbSelect.setOnCheckedChangeListener { _, _ ->
            val appPkgName: String = item.packageName
            if (holder.binding.cbSelect.isChecked) {
                hiddenApps.add(appPkgName)
            } else {
                hiddenApps.remove(appPkgName)
            }
            PrefMgr.setHideApps(hiddenApps)
            Log.d("SHOWWW", "SHOW = $hiddenApps")
        }
    }

    fun update(data: ArrayList<AppInfoUtil.AppInfo>?) {
        data?.let {
            list.clear()
            list.addAll(data)
        }
        notifyDataSetChanged()
    }
}
