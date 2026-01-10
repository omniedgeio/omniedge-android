package io.omniedge.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.omniedge.App
import io.omniedge.BuildConfig
import io.omniedge.R
import io.omniedge.databinding.ActivityLoginBinding
import io.omniedge.ui.fragment.handleLoginResult
import io.reactivex.android.schedulers.AndroidSchedulers


class LoginActivity : BaseActivity() {
    private companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var client: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    
    // UUID for session authorization after login
    var pendingSessionUUID: String? = null

    override fun getPageTitle(): Int {
        return R.string.app_login
    }

    override fun getLayoutView(): View {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init() {
        super.init()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.CLIENT_ID)
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(this, gso)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleGoogleSignInResult(task)
            }
    }

    fun signInWithGoogle(sessionUUID: String? = null) {
        pendingSessionUUID = sessionUUID
        resultLauncher.launch(client.signInIntent)
    }

    fun signInWithBrowser() {
        val loginUrl = "https://connect.omniedge.io/login/mobile"
        try {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(loginUrl))
            startActivity(intent)
        } catch (e: Exception) {
            showToast(getString(R.string.browser_missing))
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "sign in result:$account")
            val idToken = account.idToken
            if (idToken != null) {
                App.repository
                    .loginWithGoogle(idToken, pendingSessionUUID)
                    .observeOn(AndroidSchedulers.mainThread())
                    .handleLoginResult(this, this)
                
                // If there's a pending session, notify it after successful token update
                pendingSessionUUID?.let { uuid ->
                    App.repository.notifySession(uuid)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.d(TAG, "Session notified successfully")
                        }, {
                            Log.e(TAG, "Failed to notify session", it)
                        })
                }
            } else {
                showToast(getString(R.string.invalid_token))
            }
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=${e.statusCode} message=${e.message}")
            showToast("SignInResult:failed code=${e.statusCode}")
        }
    }
}