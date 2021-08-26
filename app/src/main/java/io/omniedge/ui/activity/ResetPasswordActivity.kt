package io.omniedge.ui.activity

import android.content.Intent
import android.view.View
import io.omniedge.App
import io.omniedge.BusObserver
import io.omniedge.R
import io.omniedge.data.bean.Response
import io.omniedge.databinding.ActivityResetPasswordBinding
import io.reactivex.android.schedulers.AndroidSchedulers

class ResetPasswordActivity : BaseActivity() {
    private companion object {
        private const val TAG = "ResetPasswordActivity"
    }

    private lateinit var binding: ActivityResetPasswordBinding

    override fun getLayoutView(): View {
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init() {
        super.init()
        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text?.toString()

            if (email.isNullOrBlank()) {
                toast(R.string.email_or_password_empty)
                return@setOnClickListener
            }

            resetPassword(email)
        }
    }

    private fun resetPassword(email: String) {
        App.repository.resetPassword(email)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<Response>(this@ResetPasswordActivity) {
                override fun loading(): Boolean {
                    return true
                }

                override fun onSuccess(t: Response) {
                    super.onSuccess(t)
                    try {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                        startActivity(intent)
                    } catch (e: Exception) {
                        if (!t.message.isNullOrEmpty()) {
                            showToast(t.message)
                        }
                    }
                }
            })
    }
}