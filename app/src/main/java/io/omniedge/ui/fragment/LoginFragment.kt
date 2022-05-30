package io.omniedge.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.findNavController
import io.omniedge.R
import io.omniedge.databinding.FragmentLoginBinding
import io.omniedge.ui.activity.ResetPasswordActivity

class LoginFragment : BaseLoginFragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
            .apply {
//                lifecycleOwner = viewLifecycleOwner
            }
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()
            val password = binding.etPassword.text?.toString()

            if (email.isNullOrBlank() || password.isNullOrEmpty()) {
                toast(it, R.string.email_or_password_empty)
                return@setOnClickListener
            }

            if (password.length < 8) {
                toast(it, R.string.password_short_length)
                return@setOnClickListener
            }

            signIn(email, password)
        }
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnLogin.callOnClick()
                true
            } else false
        }

        binding.tvForgetPassword?.setOnClickListener {
            startActivity(Intent(requireContext(), ResetPasswordActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            it.findNavController().navigate(R.id.register_fragment)
        }
        return binding.root
    }
}