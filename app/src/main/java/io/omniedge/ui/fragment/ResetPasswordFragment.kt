package io.omniedge.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.omniedge.App
import io.omniedge.BusObserver
import io.omniedge.R
import io.omniedge.data.bean.Response
import io.omniedge.databinding.FragmentResetPasswordBinding
import io.reactivex.android.schedulers.AndroidSchedulers

class ResetPasswordFragment : BaseFragment() {

    private lateinit var binding: FragmentResetPasswordBinding

    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater)
        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text?.toString()

            if (email.isNullOrBlank()) {
                toast(it, R.string.email_or_password_empty)
                return@setOnClickListener
            }

            resetPassword(email)
        }
        return binding.root
    }

    private fun resetPassword(email: String) {
        App.repository.resetPassword(email)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<Response>(this) {
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