package com.example.myapplication.helper

import android.content.Context

abstract class BaseHideApp(var context: Context) {

    abstract fun hide(pkgNames: Set<String?>?)

    abstract fun unHide(pkgNames: Set<String?>?)

    abstract fun tryToActivate(activationCallbackListener: ActivationCallbackListener?)

    abstract val name: String?

    interface ActivationCallbackListener {
        fun onActivateCallback(
            appHider: Class<out BaseHideApp?>?,
            success: Boolean,
            msgResID: Int
        )
    }
}