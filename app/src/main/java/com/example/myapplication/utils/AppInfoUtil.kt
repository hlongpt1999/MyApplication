package com.example.myapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.example.myapplication.feature.hide.model.AppInfo

class AppInfoUtil(context: Context) {
    private val pkgMgr: PackageManager = context.packageManager
    private val appInfoList: MutableList<AppInfo> = ArrayList()

    @SuppressLint("QueryPermissionsNeeded")
    fun refresh() {
        appInfoList.clear()

        val hiddenApps: Set<String>? = PrefMgr.hideApps

        // Get applications info
        val installedApplications =
            pkgMgr.getInstalledApplications(PackageManager.GET_META_DATA or PackageManager.MATCH_DISABLED_COMPONENTS or PackageManager.MATCH_UNINSTALLED_PACKAGES)
        for (applicationInfo in installedApplications) {
            // Filter out Amarok itself

            if (applicationInfo.packageName.contains("deltazero.amarok")) continue

            val appInfo = AppInfo(
                applicationInfo.packageName,
                pkgMgr.getApplicationLabel(applicationInfo).toString(),
                (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM,
                pkgMgr.getApplicationIcon(applicationInfo)
            )

            appInfoList.add(appInfo)
        }

        // Sort with app name, and stick the hidden apps to the top
        appInfoList.sortWith(Comparator { o1, o2 ->
            if (hiddenApps?.contains(o1.packageName) == true && !hiddenApps.contains(o2.packageName)) {
                -1
            }
            if (hiddenApps?.contains(o2.packageName) == true && !hiddenApps.contains(o1.packageName)) {
                1
            } else {
                o1.label?.compareTo(o2?.label ?: "") ?: 0
            }
        })
    }

    fun getFilteredApps(query: String?, includeSystemApps: Boolean): List<AppInfo> {
        val hiddenApps: Set<String>? = PrefMgr.hideApps
        Log.d("MyAppAction " + "AppInfoUtil", "Hidden apps: $hiddenApps")
        val queryAppInfoList: MutableList<AppInfo> = ArrayList()
        for (appInfo in appInfoList) {
            val query_filter_result = ((query == null || query.isEmpty())
                    || containsIgnoreCase(
                appInfo.packageName,
                query
            ) || containsIgnoreCase(appInfo.label, query))

            val system_filter_result =
                (hiddenApps?.contains(appInfo.packageName ?: "") == true /* If the app is hidden, show it regardless of whether it is a system app or not. */
                        || includeSystemApps /* Skip this filter if user enable `Display system apps` */
                        || !appInfo.isSystemApp)

            if (query_filter_result && system_filter_result && !appInfo.isSystemApp) queryAppInfoList.add(appInfo)
        }
        return queryAppInfoList
    }

    companion object {
        private fun containsIgnoreCase(str: String?, searchStr: String?): Boolean {
            if (str == null || searchStr == null) return false

            val length = searchStr.length
            if (length == 0) return true

            for (i in str.length - length downTo 0) {
                if (str.regionMatches(i, searchStr, 0, length, ignoreCase = true)) return true
            }
            return false
        }
    }
}