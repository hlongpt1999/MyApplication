package com.example.myapplication.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.IBinder
import android.system.Os
import android.util.Log
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.utils.Global
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.ShizukuProvider
import rikka.shizuku.SystemServiceHelper
import rikka.shizuku.shared.BuildConfig
import java.lang.reflect.Method

class CustomHideAppHelper(context: Context) : BaseHideApp(context) {
    companion object {
        val shizukuReqCode: Int = 600
    }

    init {
        ShizukuProvider.enableMultiProcessSupport(false)
    }

    @SuppressLint("PrivateApi")
    private fun setAppDisabled(disabled: Boolean, pkgNames: Set<String?>?) {
        var mSetApplicationEnabledSetting: Method? = null
        var iPmInstance: Any? = null

        try {
            val iPmClass = Class.forName("android.content.pm.IPackageManager")

            val iPmStub = Class.forName("android.content.pm.IPackageManager\$Stub")
            val asInterfaceMethod = iPmStub.getMethod(
                "asInterface",
                IBinder::class.java
            )
            iPmInstance = asInterfaceMethod.invoke(
                null,
                ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package"))
            )

            mSetApplicationEnabledSetting = iPmClass.getMethod(
                "setApplicationEnabledSetting",
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
        } catch (e: Exception) {
           Log.e("MyAppAction " + "ShizukuHider", e.toString())
            Toast.makeText(context, R.string.shizuku_hidden_api_error, Toast.LENGTH_LONG).show()
            return
        }

        pkgNames?.let {
            for (item in it) {
                try {
                    mSetApplicationEnabledSetting.invoke(
                        iPmInstance,
                        item,
                        (if (disabled) PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER else PackageManager.COMPONENT_ENABLED_STATE_DEFAULT),
                        0,
                        Os.getuid() / 100000,
                        "com.example.myapplication"
                    )
                    Log.i("MyAppAction " + "ShizukuHider", "Hide app: $item")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.w("MyAppAction " +"ShizukuHider", e.toString())
                }
            }
        }
    }

    @SuppressLint("PrivateApi")
    private fun setAppHidden(hidden: Boolean, pkgNames: Set<String?>?) {
        /*
    Call android.content.pm.IPackageManager.setApplicationHiddenSettingAsUser with reflection.
    Via Shizuku wrapper.
     */

        var mSetApplicationHiddenSettingAsUser: Method? = null
        var iPmInstance: Any? = null

        try {
            val iPmClass = Class.forName("android.content.pm.IPackageManager")

            val iPmStub = Class.forName("android.content.pm.IPackageManager\$Stub")
            val asInterfaceMethod = iPmStub.getMethod("asInterface", IBinder::class.java)
            iPmInstance = asInterfaceMethod.invoke(
                null,
                ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package"))
            )

            mSetApplicationHiddenSettingAsUser = iPmClass.getMethod(
                "setApplicationHiddenSettingAsUser",
                String::class.java,
                Boolean::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
        } catch (e: Exception) {
            e.printStackTrace()
           Log.e("MyAppAction " + "ShizukuHider", e.toString())
            Toast.makeText(context, R.string.shizuku_hidden_api_error, Toast.LENGTH_LONG).show()
            return
        }

        pkgNames?.let {
            for (p in it) {
                try {
                    mSetApplicationHiddenSettingAsUser.invoke(
                        iPmInstance,
                        p,
                        hidden,
                        Os.getuid() / 100000
                    )
                    Log.i("MyAppAction " + "ShizukuHider", "Hid app: $p")
                    Global.showFeature = true
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Log.w("MyAppAction " +"ShizukuHider", e.toString())
                }
            }
        }
    }

    override fun hide(pkgNames: Set<String?>?) {
        if (!Shizuku.pingBinder()) {
            Log.w("MyAppAction " +"ShizukuHider", "Binder not available.")
            return
        }
        setAppDisabled(true, pkgNames)
        setAppHidden(true, pkgNames)
    }

    override fun unHide(pkgNames: Set<String?>?) {
        if (!Shizuku.pingBinder()) {
            Log.w("MyAppAction " +"ShizukuHider", "Binder not available.")
            return
        }
        setAppDisabled(false, pkgNames)
        setAppHidden(false, pkgNames)
    }

    override fun tryToActivate(activationCallbackListener: ActivationCallbackListener?) {
        try {
            if (Shizuku.isPreV11()) {
                Log.w("MyAppAction " +"ShizukuHider", "checkAvailability: Shizuku is running pre v11.")
                activationCallbackListener?.onActivateCallback(
                    this.javaClass,
                    false,
                    R.string.shizuku_pre_v11
                )
                return
            }

            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                if (Shizuku.pingBinder()) {
                    Log.i("MyAppAction " + "ShizukuHider", "checkAvailability: Shizuku available.")
                    activationCallbackListener?.onActivateCallback(this.javaClass, true, 0)
                } else {
                    Log.w("MyAppAction " +"ShizukuHider", "checkAvailability: Binder not available.")
                    activationCallbackListener?.onActivateCallback(
                        this.javaClass,
                        false,
                        R.string.shizuku_service_not_running
                    )
                }
                return
            }

            if (Shizuku.shouldShowRequestPermissionRationale()) {
                // Users choose "Deny and don't ask again"
                Log.w("MyAppAction " +"ShizukuHider", "checkAvailability: permission denied.")
                activationCallbackListener?.onActivateCallback(
                    this.javaClass,
                    false,
                    R.string.shizuku_permission_denied
                )
                return
            }

            // Request permission
            val listener: OnRequestPermissionResultListener =
                object : OnRequestPermissionResultListener {
                    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            Log.i("MyAppAction " + 
                                "ShizukuHider",
                                "Permission granted. Set hider to ShizukuHider."
                            )
                            activationCallbackListener!!.onActivateCallback(
                                CustomHideAppHelper::class.java,
                                true,
                                0
                            )
                        } else {
                            Log.i("MyAppAction " + "ShizukuHider", "Permission denied.")
                            activationCallbackListener!!.onActivateCallback(
                                CustomHideAppHelper::class.java,
                                false,
                                R.string.shizuku_permission_denied
                            )
                        }
                        Shizuku.removeRequestPermissionResultListener(this)
                    }
                }
            Shizuku.addRequestPermissionResultListener(listener)
            Shizuku.requestPermission(shizukuReqCode)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("MyAppAction " +"ShizukuHider", "checkAvailability: Shizuku not available: ", e)
            activationCallbackListener!!.onActivateCallback(
                this.javaClass,
                false,
                R.string.shizuku_not_working
            )
        }
    }

    override val name: String = "Custom"
}
