package com.example.myapplication

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.databinding.ActivityHideBinding

class HideActivity : Activity() {

    private lateinit var binding: ActivityHideBinding
    private val REQUEST_CODE_ENABLE_ADMIN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ComponentName(this, MyDeviceAdminReceiver::class.java))
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Your explanation for why this permission is needed")
        startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)

        binding.btnShow.setOnClickListener {
            val installedPackages = getInstalledPackages()
            for (packageName in installedPackages) {
                Log.d("MyAppAction " + "InstalledPackage", "Package Name: $packageName")
            }
        }

        binding.btnHidePackage.setOnClickListener {
            val installedPackages = getInstalledPackages()
            for (packageName in installedPackages) {
                hidePackageApp(packageName)
            }
        }
    }

    private fun getInstalledPackages(): List<String> {
        val packageManager = this.packageManager
        val installedApps = mutableListOf<String>()
        val packages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
        for (packageInfo in packages) {
                installedApps.add(packageInfo.packageName)
        }
        return installedApps
    }

    private fun hidePackageApp(packageName: String) {
        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = ComponentName(this, MyDeviceAdminReceiver::class.java)

        try {
            // Toggle hidden status

            devicePolicyManager.setApplicationHidden(componentName, packageName, true)

//            // Check if application is hidden
//            val isHidden = devicePolicyManager.isApplicationHidden(componentName, packageName)
//
//            // Optionally, notify user or update UI based on new hidden status
//            if (!isHidden) {
//                Toast.makeText(this, "Application unhidden", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "Application hidden", Toast.LENGTH_SHORT).show()
//            }
        } catch (e: SecurityException) {
            // Handle exception
            e.printStackTrace()
            Toast.makeText(this, "Device admin permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            if (resultCode == Activity.RESULT_OK) {
                // Device admin enabled
                // Now you can use DevicePolicyManager to manage device policies
                Toast.makeText(this, "RESULT_OK", Toast.LENGTH_SHORT).show()
            } else {
                // Device admin not enabled
                // Handle accordingly (e.g., inform user or retry)
                Toast.makeText(this, "Not OK", Toast.LENGTH_SHORT).show()
            }
        }
    }

}