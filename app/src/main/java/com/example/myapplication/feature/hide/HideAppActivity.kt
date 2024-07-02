package com.example.myapplication.feature.hide

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myapplication.databinding.ActivityHideAppBinding
import com.example.myapplication.utilsjava.AppInfoUtil
import com.example.myapplication.utilsjava.AppInfoUtil.AppInfo
import com.example.myapplication.utilsjava.Hider
import java.util.Locale

class HideAppActivity : Activity(), View.OnClickListener {

    private lateinit var binding: ActivityHideAppBinding
    private lateinit var adapter: AppListAdapter
    private var backgroundHandler: Handler? = null
    private var appInfoUtil: AppInfoUtil? = null
    private var dataAppInfo: List<AppInfo>? = null

    override fun onClick(p0: View?) {
        when (p0) {
            binding.btnHide -> {
                Hider.hide(this)
            }

            binding.btnUnHide -> {
                Hider.unhide(this)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHideAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        initEvent()
        getListInstalledPackage()
    }

    private fun initUI() {
        adapter = AppListAdapter(ArrayList())
        binding.rcvPackage.adapter = adapter
        appInfoUtil = AppInfoUtil(this)
    }

    private fun initEvent() {
        binding.btnHide.setOnClickListener(this)
        binding.btnUnHide.setOnClickListener(this)
    }

    private fun getListInstalledPackage() {
        backgroundHandler = Handler(Looper.getMainLooper())
        backgroundHandler?.post(
            Runnable {
                appInfoUtil?.refresh()
                this.runOnUiThread {
                    dataAppInfo = appInfoUtil?.getFilteredApps(null, true)
                    adapter.update(ArrayList(dataAppInfo))
                    Log.d("MyAppAction " + "SHOW", "SHOW = $dataAppInfo")
                }
            })
    }

}