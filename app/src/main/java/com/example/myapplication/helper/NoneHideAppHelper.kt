package com.example.myapplication.helper

import android.content.Context
import android.util.Log

class NoneHideAppHelper(context: Context) : BaseHideApp(context) {
    override fun hide(pkgNames: Set<String?>?) {
        Log.w("MyAppAction " +"AppHider", "Skip app hiding: hider disabled")
    }

    override fun unHide(pkgNames: Set<String?>?) {
        Log.w("MyAppAction " +"AppHider", "Skip app unhiding: hider disabled")
    }

    override fun tryToActivate(activationCallbackListener: ActivationCallbackListener?) {
        activationCallbackListener?.onActivateCallback(javaClass, true, 0)
    }

    override val name: String
        get() = "Disabled"
}
