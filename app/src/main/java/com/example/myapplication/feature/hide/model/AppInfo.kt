package com.example.myapplication.feature.hide.model

import android.graphics.drawable.Drawable

data class AppInfo(
    var packageName: String? = null,
    var label: String ?= null,
    var isSystemApp: Boolean = false,
    var icon: Drawable? = null)
