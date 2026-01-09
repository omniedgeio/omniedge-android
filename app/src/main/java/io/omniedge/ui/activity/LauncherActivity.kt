package io.omniedge.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import io.omniedge.App
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
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data
        if (data != null && data.scheme == "io.omniedge") {
            val code = data.getQueryParameter("code")
            val state = data.getQueryParameter("state")
            val verifier = App.repository.getPKCEVerifier()
            val savedState = App.repository.getPKCEState()

            if (code != null && state != null && verifier != null && state == savedState) {
                App.repository.clearPKCE()
                lifecycleScope.launch {
                    try {
                        val result = App.repository.exchangeOAuthCode(code, verifier, "io.omniedge:/oauth/callback").blockingGet()
                        if (result.data?.token != null) {
                            launchMain()
                            finish()
                        } else {
                            launchLogin()
                            finish()
                        }
                    } catch (e: Exception) {
                        launchLogin()
                        finish()
                    }
                }
                return
            }
        }

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