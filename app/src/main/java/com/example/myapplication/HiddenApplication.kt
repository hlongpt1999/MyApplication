package com.example.myapplication

import android.app.Application
import com.example.myapplication.utilsjava.Hider
import com.example.myapplication.utilsjava.PrefMgr

class HiddenApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        PrefMgr.init(this)
        Hider.init()
    }
}