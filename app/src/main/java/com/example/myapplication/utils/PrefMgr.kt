package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.example.myapplication.helper.BaseHideApp
import com.example.myapplication.helper.CustomHideAppHelper
import com.example.myapplication.helper.NoneHideAppHelper

object PrefMgr {
    var prefs: SharedPreferences? = null
        private set
    private var mPrefEditor: SharedPreferences.Editor? = null
    val MAIN_PREF_FILENAME: String = "deltazero.amarok.prefs"
    var initialized: Boolean = false

    /**
     * This method should be invoked in [AmarokApplication.onCreate].
     *
     * @param context Application context
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(MAIN_PREF_FILENAME, Context.MODE_PRIVATE)
        mPrefEditor = prefs?.edit()
        initialized = true
    }

    val HIDE_FILE_PATH: String = "hideFilePath"
    val IS_HIDDEN: String = "isHidden"
    val HIDE_PKG_NAMES: String = "hidePkgNames"
    val APP_HIDER_MODE: String = "appHiderMode"
    val FILE_HIDER_MODE: String = "fileHiderMode"
    val IS_ENABLE_AUTO_UPDATE: String = "isEnableAutoUpdate"
    val ENABLE_OBFUSCATE_FILE_HEADER: String = "enableObfuscateFileHeader"
    val ENABLE_OBFUSCATE_TEXT_FILE: String = "enableObfuscateTextFile"
    val ENABLE_OBFUSCATE_TEXT_FILE_ENHANCED: String = "enableObfuscateTextFileEnhanced"
    val ENABLE_QUICK_HIDE_SERVICE: String = "enableQuickHideService"
    val ENABLE_PANIC_BUTTON: String = "enablePanicButton"
    val AMAROK_PASSWORD: String = "amarokPassword"
    val ENABLE_AMAROK_BIOMETRIC_AUTH: String = "enableAmarokBiometricAuth"
    val ENABLE_DYNAMIC_COLOR: String = "enableDynamicColor"
    val ENABLE_DISGUISE: String = "enableDisguise"
    val DO_SHOW_QUIT_DISGUISE_INSTRUCT: String = "doShowQuitDisguiseInstuct"
    val SHOW_WELCOME: String = "showWelcome"
    val ENABLE_AUTO_HIDE: String = "enableAutoHide"
    val AUTO_HIDE_DELAY: String = "autoHideDelay"
    val BLOCK_SCREENSHOTS: String = "blockScreenshots"
    val ENABLE_X_HIDE: String = "enableXHide"

    var hideFilePath: Set<String>?
        get() = prefs!!.getStringSet(HIDE_FILE_PATH, HashSet())
        set(path) {
            mPrefEditor!!.putStringSet(HIDE_FILE_PATH, path)
            mPrefEditor!!.apply()
        }

    var isHidden: Boolean
        /**
         * Avoid using this method except for initializing [Hider].
         * Use [Hider.getState] instead.
         */
        get() = prefs!!.getBoolean(IS_HIDDEN, false)
        set(isHidden) {
            mPrefEditor!!.putBoolean(IS_HIDDEN, isHidden)
            mPrefEditor!!.apply()
        }

    var hideApps: Set<String>?
        get() = prefs!!.getStringSet(HIDE_PKG_NAMES, HashSet())
        set(pkgNames) {
            mPrefEditor!!.putStringSet(HIDE_PKG_NAMES, pkgNames)
            mPrefEditor!!.apply()
        }

    fun getAppHider(context: Context): BaseHideApp {
        return when (prefs!!.getInt(APP_HIDER_MODE, 0)) {
            0 -> NoneHideAppHelper(context)
            3 -> CustomHideAppHelper(context)
            else -> throw IndexOutOfBoundsException("Should not reach here")
        }
    }

    fun setAppHiderMode(mode: Class<out BaseHideApp?>) {
        val modeCode: Int
        if (mode == NoneHideAppHelper::class.java) modeCode = 0
        else if (mode == CustomHideAppHelper::class.java) modeCode = 3
        else throw IndexOutOfBoundsException("Should not reach here")
        mPrefEditor!!.putInt(APP_HIDER_MODE, modeCode)
        mPrefEditor!!.apply()
    }

//    fun getFileHider(context: Context?): BaseFileHider {
//        return when (prefs!!.getInt(FILE_HIDER_MODE, 1)) {
//            0 -> NoneFileHider(context)
//            1 -> ObfuscateFileHider(context)
//            2 -> NoMediaFileHider(context)
//            3 -> ChmodFileHider(context)
//            else -> throw IndexOutOfBoundsException("Should not reach here")
//        }
//    }
//
//    fun setFileHiderMode(mode: Class<out BaseFileHider?>) {
//        val modeCode: Int
//        if (mode == NoneFileHider::class.java) modeCode = 0
//        else if (mode == ObfuscateFileHider::class.java) modeCode = 1
//        else if (mode == NoMediaFileHider::class.java) modeCode = 2
//        else if (mode == ChmodFileHider::class.java) modeCode = 3
//        else throw IndexOutOfBoundsException("Should not reach here")
//        mPrefEditor!!.putInt(FILE_HIDER_MODE, modeCode)
//        mPrefEditor!!.apply()
//    }

    var enableAutoUpdate: Boolean
        get() = prefs!!.getBoolean(IS_ENABLE_AUTO_UPDATE, true)
        set(isEnable) {
            mPrefEditor!!.putBoolean(IS_ENABLE_AUTO_UPDATE, isEnable)
            mPrefEditor!!.apply()
        }

    var enableObfuscateFileHeader: Boolean
        get() = prefs!!.getBoolean(ENABLE_OBFUSCATE_FILE_HEADER, false)
        set(ifObfuscateFileHeader) {
            mPrefEditor!!.putBoolean(ENABLE_OBFUSCATE_FILE_HEADER, ifObfuscateFileHeader)
            mPrefEditor!!.apply()
        }

    var enableObfuscateTextFile: Boolean
        get() = prefs!!.getBoolean(ENABLE_OBFUSCATE_TEXT_FILE, false)
        set(ifObfuscateTextFile) {
            mPrefEditor!!.putBoolean(ENABLE_OBFUSCATE_TEXT_FILE, ifObfuscateTextFile)
            mPrefEditor!!.apply()
        }

    var enableObfuscateTextFileEnhanced: Boolean
        get() = prefs!!.getBoolean(ENABLE_OBFUSCATE_TEXT_FILE_ENHANCED, false)
        set(ifObfuscateTextFileEnhanced) {
            mPrefEditor!!.putBoolean(
                ENABLE_OBFUSCATE_TEXT_FILE_ENHANCED,
                ifObfuscateTextFileEnhanced
            )
            mPrefEditor!!.apply()
        }

    var enableQuickHideService: Boolean
        get() = prefs!!.getBoolean(ENABLE_QUICK_HIDE_SERVICE, false)
        set(isEnableQuickHideService) {
            mPrefEditor!!.putBoolean(ENABLE_QUICK_HIDE_SERVICE, isEnableQuickHideService)
            mPrefEditor!!.apply()
        }

    var enablePanicButton: Boolean
        get() = prefs!!.getBoolean(ENABLE_PANIC_BUTTON, false)
        set(isEnablePanicButton) {
            mPrefEditor!!.putBoolean(ENABLE_PANIC_BUTTON, isEnablePanicButton)
            mPrefEditor!!.apply()
        }

    var amarokPassword: String?
        get() = prefs!!.getString(AMAROK_PASSWORD, null)
        set(password) {
            mPrefEditor!!.putString(AMAROK_PASSWORD, password)
            mPrefEditor!!.apply()
        }

    var enableAmarokBiometricAuth: Boolean
        get() = prefs!!.getBoolean(ENABLE_AMAROK_BIOMETRIC_AUTH, false)
        set(enableAmarokBiometricAuth) {
            mPrefEditor!!.putBoolean(ENABLE_AMAROK_BIOMETRIC_AUTH, enableAmarokBiometricAuth)
            mPrefEditor!!.apply()
        }

    var enableDynamicColor: Boolean
        get() = prefs!!.getBoolean(
            ENABLE_DYNAMIC_COLOR,
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        )
        set(enableDynamicColor) {
            mPrefEditor!!.putBoolean(ENABLE_DYNAMIC_COLOR, enableDynamicColor)
            mPrefEditor!!.apply()
        }

    var enableDisguise: Boolean
        get() {
            return prefs!!.getBoolean(ENABLE_DISGUISE, false)
        }
        set(enableDisguise) {
            mPrefEditor!!.putBoolean(ENABLE_DISGUISE, enableDisguise)
            mPrefEditor!!.apply()
        }

    var doShowQuitDisguiseInstuct: Boolean
        get() {
            return prefs!!.getBoolean(DO_SHOW_QUIT_DISGUISE_INSTRUCT, true)
        }
        set(doShowQuitDisguiseInstuct) {
            mPrefEditor!!.putBoolean(DO_SHOW_QUIT_DISGUISE_INSTRUCT, doShowQuitDisguiseInstuct)
            mPrefEditor!!.apply()
        }

    var showWelcome: Boolean
        get() {
            return prefs!!.getBoolean(SHOW_WELCOME, true)
        }
        set(showWelcome) {
            mPrefEditor!!.putBoolean(SHOW_WELCOME, showWelcome)
            mPrefEditor!!.apply()
        }

    var enableAutoHide: Boolean
        get() {
            return prefs!!.getBoolean(ENABLE_AUTO_HIDE, false)
        }
        set(enableAutoHide) {
            mPrefEditor!!.putBoolean(ENABLE_AUTO_HIDE, enableAutoHide)
            mPrefEditor!!.apply()
        }

    var autoHideDelay: Int
        get() {
            return prefs!!.getInt(AUTO_HIDE_DELAY, 0)
        }
        set(autoHideDelay) {
            mPrefEditor!!.putInt(AUTO_HIDE_DELAY, autoHideDelay)
            mPrefEditor!!.apply()
        }

    var blockScreenshots: Boolean
        get() {
            return prefs!!.getBoolean(BLOCK_SCREENSHOTS, false)
        }
        set(blockScreenshots) {
            mPrefEditor!!.putBoolean(BLOCK_SCREENSHOTS, blockScreenshots)
            mPrefEditor!!.apply()
        }

    var isXHideEnabled: Boolean
        get() {
            return prefs!!.getBoolean(ENABLE_X_HIDE, false)
        }
        set(enableXHide) {
            mPrefEditor!!.putBoolean(ENABLE_X_HIDE, enableXHide)
            mPrefEditor!!.apply()
        }
}