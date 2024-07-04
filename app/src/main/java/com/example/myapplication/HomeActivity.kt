package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.feature.hide.HideAppActivity
import com.example.myapplication.feature.pairing.AdbPairingActivity
import com.example.myapplication.feature.pairing.StarterActivity
import com.example.myapplication.feature.pairing.utils.EnvironmentUtils
import com.example.myapplication.helper.BaseHideApp
import com.example.myapplication.helper.CustomHideAppHelper
import com.example.myapplication.helper.NoneHideAppHelper
import com.example.myapplication.utilsjava.PrefMgr
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import rikka.core.content.asActivity
import java.util.Locale


class HomeActivity : Activity(), View.OnClickListener, BaseHideApp.ActivationCallbackListener {

    private lateinit var binding: ActivityHomeBinding

    override fun onClick(p0: View?) {
        when (p0) {
            binding.llPairingFeature -> {
                startActivity(Intent(this, AdbPairingActivity::class.java))
            }
            binding.llStartFeature -> {
                val port = EnvironmentUtils.getAdbTcpPort()
//                if (port > 0) {
                    val host = "127.0.0.1"
                    val intent = Intent(this, StarterActivity::class.java).apply {
                        putExtra(StarterActivity.EXTRA_IS_ROOT, false)
                        putExtra(StarterActivity.EXTRA_HOST, host)
                        putExtra(StarterActivity.EXTRA_PORT, port)
                    }
                    startActivity(intent)
//                }
                //                else {
//                    WadbNotEnabledDialogFragment().show(context.asActivity<FragmentActivity>().supportFragmentManager)
//                }
            }
            binding.llHideFeature -> {
                CustomHideAppHelper(this).tryToActivate(this)
            }
            binding.llChangeLanguage -> {
                changeLanguage("vi")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initEvent()
    }

    private fun initEvent() {
        binding.llPairingFeature.setOnClickListener(this)
        binding.llHideFeature.setOnClickListener(this)
        binding.llChangeLanguage.setOnClickListener(this)
        binding.llStartFeature.setOnClickListener(this)
    }

    private fun changeLanguage(languageCode: String) {
        val locale: Locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config: Configuration = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        // You may need to recreate the activity for the language change to take effect
        recreate()

        // Optionally, you can show a toast or message to indicate language change
        Toast.makeText(this, "Language changed to $languageCode", Toast.LENGTH_SHORT).show()
    }

    override fun onActivateCallback(
        appHider: Class<out BaseHideApp?>?,
        success: Boolean,
        msgResID: Int
    ) {
        if (success) {
            appHider?.let {
                PrefMgr.setAppHiderMode(appHider)
            }
            startActivity(Intent(this, HideAppActivity::class.java))
        } else {
            assert(msgResID != 0)

            PrefMgr.setAppHiderMode(NoneHideAppHelper::class.java)

            runOnUiThread {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.apphider_not_ava_title)
                    .setMessage(msgResID)
                    .setPositiveButton(getString(R.string.ok), null)
                    .setNegativeButton(R.string.help) { dialog, which ->
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.common_error_doc_url))
                            )
                        )
                    }
                    .show()
            }
        }
    }
}