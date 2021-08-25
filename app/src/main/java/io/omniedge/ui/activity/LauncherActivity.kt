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
            if (App.repository.getToken().isNullOrBlank()) launchLogin() else launchMain()
            finish()
        }
    }

    private fun launchLogin() {
        startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
    }

    private fun launchMain() {
        startActivity(Intent(this@LauncherActivity, DeviceListActivity::class.java))
    }
}