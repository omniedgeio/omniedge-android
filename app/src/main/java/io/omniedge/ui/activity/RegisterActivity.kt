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
import io.omniedge.data.bean.Register
import io.omniedge.databinding.ActivityRegisterBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.HttpException

class RegisterActivity : BaseActivity() {
    private companion object {
        private const val TAG = "RegisterActivity"
    }

    private lateinit var binding: ActivityRegisterBinding

    override fun getLayoutView(): View {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init() {
        super.init()
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text?.toString()
            val email = binding.etEmail.text?.toString()
            val password = binding.etPassword.text?.toString()
            val passwordConfirm = binding.etPasswordConfirm.text?.toString()

            if (name.isNullOrEmpty()) {
                toast(R.string.username_empty)
                return@setOnClickListener
            }

            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                toast(R.string.email_or_password_empty)
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                toast(R.string.password_confirm_mismatch)
                return@setOnClickListener
            }

            if (password.length < 8) {
                toast(R.string.password_short_length)
                return@setOnClickListener
            }

            signUp(name, email, password, passwordConfirm)
        }
        binding.etPasswordConfirm.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnRegister.callOnClick()
                true
            } else false
        }
        binding.googleLogin.setOnClickListener {
//            signInWithSocialAccount(AuthProvider.google())
        }
    }

    private fun signUp(name: String, email: String, password: String, passwordConfirm: String) {
        App.repository.register(Register(name, email, password, passwordConfirm))
            .flatMap { App.repository.login(email, password) }
            .doOnSuccess {
                App.repository.updateToken(it.data?.token)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<LoginResponse>(this@RegisterActivity) {
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
        startActivity(Intent(this@RegisterActivity, DeviceListActivity::class.java))
        finish()
    }
}