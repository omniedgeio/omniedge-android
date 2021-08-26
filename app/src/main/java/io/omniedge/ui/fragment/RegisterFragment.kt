package io.omniedge.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import io.omniedge.App
import io.omniedge.R
import io.omniedge.data.bean.Register
import io.omniedge.databinding.FragmentRegisterBinding

class RegisterFragment : BaseLoginFragment() {
    private lateinit var binding: FragmentRegisterBinding
    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        return binding.root
    }

    private fun signUp(name: String, email: String, password: String, passwordConfirm: String) {
        App.repository.register(Register(name, email, password, passwordConfirm))
            .flatMap { App.repository.login(email, password) }
            .handleLoginResult(this, requireContext())
    }
}