package io.omniedge.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import io.omniedge.App
import io.omniedge.DeviceListActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * launcher
 * Created on 2019-12-17 13:03
 *
 */
class LauncherActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            delay(1000)
            if (App.repository.getToken().isNullOrBlank()) launchSignIn() else launch()
            finish()
        }
    }

    private fun launchSignIn() {
        startActivity(Intent(this@LauncherActivity, SignInActivity::class.java))
    }

    private fun launch() {
        startActivity(Intent(this@LauncherActivity, DeviceListActivity::class.java))
    }
}