package com.example.myapplication

import android.app.Application
import android.content.Context
import com.example.myapplication.feature.pairing.utils.ShizukuSettings
import com.example.myapplication.utilsjava.Hider
import com.example.myapplication.utilsjava.PrefMgr

class HiddenApplication: Application() {

    companion object {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        PrefMgr.init(this)
        Hider.init()
        appContext = applicationContext
        ShizukuSettings.initialize(this)
    }
}