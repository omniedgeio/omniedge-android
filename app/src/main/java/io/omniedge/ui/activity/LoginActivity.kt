package io.omniedge.ui.activity

import android.view.View
import io.omniedge.R
import io.omniedge.databinding.ActivityLoginBinding


class LoginActivity : BaseActivity() {
    private companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding

    override fun getPageTitle(): Int {
        return R.string.app_login
    }

    override fun getLayoutView(): View {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding.root
    }

}