package io.omniedge.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import io.omniedge.App
import io.omniedge.BusObserver
import io.omniedge.DeviceListActivity
import io.omniedge.R
import io.omniedge.data.bean.LoginResponse
import io.omniedge.databinding.ActivitySignInBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.HttpException

class SignInActivity : BaseActivity() {
    private companion object {
        private const val TAG = "SignInActivity"
    }

    private lateinit var binding: ActivitySignInBinding
    override fun getPageTitle(): Int {
        return R.string.app_sign_in
    }

    override fun getLayoutView(): View {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init() {
        super.init()
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text?.toString()
            val password = binding.etPassword.text?.toString()

            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                toast(R.string.email_or_password_empty)
                return@setOnClickListener
            }

            if (password.length < 8) {
                toast(R.string.password_short_length)
                return@setOnClickListener
            }

            signIn(email, password)
        }
        binding.btnSignUp.setOnClickListener {
        }
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnSignIn.callOnClick()
                true
            } else false
        }
        binding.googleSignIn.setOnClickListener {
//            signInWithSocialAccount(AuthProvider.google())
        }
    }


    private fun signIn(email: String, password: String) {
        App.repository
            .login(email, password)
            .doOnSuccess {
                App.repository.updateToken(it.data?.token)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<LoginResponse>(this@SignInActivity) {
                override fun loading(): Boolean {
                    return true
                }

                override fun onSuccess(t: LoginResponse) {
                    super.onSuccess(t)
                    Log.i(TAG, "signIn onSuccess: result ${t.data?.token}")
                    if (t.data?.token.isNullOrBlank()) {
                        showToast(getString(R.string.invalid_token))
                        return
                    }
                    launch()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    if (e is HttpException) {
                        App.repository.updateToken(null)
                    }
                }
            })
    }

    private fun launch() {
        startActivity(Intent(this@SignInActivity, DeviceListActivity::class.java))
        finish()
    }
}