package io.omniedge.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.omniedge.databinding.FragmentLoginBinding
import io.omniedge.ui.activity.LoginActivity

class LoginFragment : BaseLoginFragment() {
    private lateinit var binding: FragmentLoginBinding
    
    override fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.btnGoogleSignIn.setOnClickListener {
            (requireActivity() as? LoginActivity)?.signInWithGoogle()
        }

        binding.btnBrowserLogin.setOnClickListener {
            (requireActivity() as? LoginActivity)?.signInWithBrowser()
        }

        binding.btnScanQr.setOnClickListener {
            com.google.zxing.integration.android.IntentIntegrator.forSupportFragment(this)
                .setDesiredBarcodeFormats(com.google.zxing.integration.android.IntentIntegrator.QR_CODE)
                .setPrompt("Scan QR code on your Android TV or Apple TV")
                .setBeepEnabled(true)
                .setOrientationLocked(false)
                .initiateScan()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
            if (io.omniedge.App.repository.isLoggedIn()) {
                // Already logged in - just notify session
                io.omniedge.App.repository.notifySession(sessionUUID)
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe({
                        toast(binding.root, "TV authorized successfully!")
                    }, {
                        toast(binding.root, "Failed to authorize TV: ${it.message}")
                    })
            } else {
                // Not logged in - trigger Google sign-in and save pending session
                toast(binding.root, "Please sign in with Google first")
                (requireActivity() as? LoginActivity)?.signInWithGoogle(sessionUUID)
            }
        } else {
            toast(binding.root, "Invalid QR code")
        }
    }

}