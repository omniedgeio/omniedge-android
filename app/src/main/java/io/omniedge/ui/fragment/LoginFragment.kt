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

        binding.btnScanQr.setOnClickListener {
            com.google.zxing.integration.android.IntentIntegrator.forSupportFragment(this)
                .setDesiredBarcodeFormats(com.google.zxing.integration.android.IntentIntegrator.QR_CODE)
                .setPrompt("Scan QR code on your desktop")
                .setBeepEnabled(true)
                .setOrientationLocked(false)
                .initiateScan()
        }
        binding.btnBrowserLogin.setOnClickListener {
            val pkce = io.omniedge.data.util.PKCE.generate()
            io.omniedge.App.repository.savePKCE(pkce.verifier, pkce.state)

            val clientId = "omniedge-android"
            val redirectUri = "io.omniedge:/oauth/callback"
            val baseURL = "https://api.omniedge.io/api/v2" // Should ideally come from config

            val authURL = "$baseURL/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri&response_type=code&scope=openid%20profile%20email%20offline_access&state=${pkce.state}&code_challenge=${pkce.challenge}&code_challenge_method=S256"

            val builder = androidx.browser.customtabs.CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), android.net.Uri.parse(authURL))
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        val result = com.google.zxing.integration.android.IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                handleScannedCode(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleScannedCode(code: String) {
        val uri = android.net.Uri.parse(code)
        val sessionUUID = uri.getQueryParameter("auth_session_uuid")

        if (sessionUUID != null) {
            val email = binding.etEmail.text?.toString()
            val password = binding.etPassword.text?.toString()

            if (!email.isNullOrBlank() && !password.isNullOrEmpty()) {
                signIn(email, password, sessionUUID)
            } else {
                toast(binding.root, "Please enter your credentials first")
            }
        }
    }
}