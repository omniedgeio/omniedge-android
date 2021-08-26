package io.omniedge.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.omniedge.*
import io.omniedge.data.bean.LoginResponse
import io.omniedge.databinding.ActivityLoginBinding
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.HttpException


class LoginActivity : BaseActivity() {
    private companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var client: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun getPageTitle(): Int {
        return R.string.app_login
    }

    override fun getLayoutView(): View {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init() {
        super.init()
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()
            val password = binding.etPassword.text?.toString()

            if (email.isNullOrBlank() || password.isNullOrEmpty()) {
                toast(R.string.email_or_password_empty)
                return@setOnClickListener
            }

            if (password.length < 8) {
                toast(R.string.password_short_length)
                return@setOnClickListener
            }

            signIn(email, password)
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnLogin.callOnClick()
                true
            } else false
        }
        binding.googleLogin.setOnClickListener {
            signInWithGoogle()
        }
        binding.tvForgetPassword?.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java))
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.CLIENT_ID)
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(this, gso)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
    }

    private fun signInWithGoogle() {
        resultLauncher.launch(client.signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            OmniLog.d("sign in result:$account")
            val idToken = account.idToken
            if (idToken != null) {
                App.repository
                    .loginWithGoogle(idToken)
                    .handleLoginResult()
            } else {
                showToast(getString(R.string.invalid_token))
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            showToast("SignInResult:failed code=${e.statusCode}")
        }
    }

    private fun signIn(email: String, password: String) {
        App.repository
            .login(email, password)
            .handleLoginResult()
    }

    private fun launch() {
        startActivity(Intent(this@LoginActivity, DeviceListActivity::class.java))
        finish()
    }

    private fun Single<LoginResponse>.handleLoginResult() {
        this.doOnSuccess {
            App.repository.updateToken(it.data?.token)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<LoginResponse>(this@LoginActivity) {
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
}