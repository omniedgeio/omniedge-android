package io.omniedge.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.amazonaws.AmazonServiceException
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.auth.result.step.AuthSignInStep
import com.amplifyframework.auth.result.step.AuthSignUpStep
import com.amplifyframework.core.Amplify
import io.omniedge.BusObserver
import io.omniedge.DeviceListActivity
import io.omniedge.R
import io.omniedge.data.DataRepository
import io.omniedge.databinding.ActivitySignInBinding
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class SignInActivity : BaseActivity() {
    private companion object {
        private const val TAG = "SignInActivity"
        private var showConfirmButton = false
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
        updateConfirmButton()
        binding.tilPasswordConfirm.visibility =
            if (showConfirmButton) View.VISIBLE else View.INVISIBLE
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text?.toString()
            val password = binding.etPassword.text?.toString()
            showConfirmButton = false
            updateConfirmButton()

            signIn(email, password)
        }
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text?.toString()
            val password = binding.etPassword.text?.toString()

            showConfirmButton = true
            if (updateConfirmButton()) {
                return@setOnClickListener
            }
            val passwordConfirm = binding.etPasswordConfirm.text?.toString()

            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                toast(R.string.email_or_password_empty)
                return@setOnClickListener
            }

            if (password.length < 8) {
                toast(R.string.password_short_length)
                return@setOnClickListener
            }
            if (passwordConfirm != password) {
                toast(R.string.password_confirm_mismatch)
                return@setOnClickListener
            }
            signUp(email, email, password)
        }
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnSignIn.callOnClick()
                true
            } else false
        }
        binding.etPasswordConfirm.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnSignUp.callOnClick()
                true
            } else false
        }
        binding.googleSignIn.setOnClickListener {
            signInWithSocialAccount(AuthProvider.google())
        }
    }

    private fun updateConfirmButton(): Boolean {
        if (showConfirmButton && binding.tilPasswordConfirm.visibility != View.VISIBLE) {
            binding.tilPasswordConfirm.visibility = View.VISIBLE
            binding.etPasswordConfirm.requestFocus()
            binding.etPassword.imeOptions = EditorInfo.IME_ACTION_NEXT
            return true
        } else if (!showConfirmButton && binding.tilPasswordConfirm.visibility != View.INVISIBLE) {
            binding.tilPasswordConfirm.visibility = View.INVISIBLE
            binding.etPassword.imeOptions = EditorInfo.IME_ACTION_DONE
            binding.etPasswordConfirm.text.clear()
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
            Amplify.Auth.handleWebUISignInResponse(data)
        }
    }

    private fun signInWithSocialAccount(provider: AuthProvider) {
        DataRepository.getInstance(this)
            .amplifySignInWithSocialAccount(provider, this)
            .apply { handleSignInResult(this) }
    }

    private fun signIn(email: String?, password: String?) {
        DataRepository.getInstance(this)
            .amplifySignIn(email, password)
            .apply { handleSignInResult(this) }
    }

    private fun handleSignInResult(signInRequest: Single<AuthSignInResult>) {
        signInRequest.observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<AuthSignInResult>(this) {
                override fun loading(): Boolean {
                    return true
                }

                override fun onSuccess(t: AuthSignInResult) {
                    super.onSuccess(t)
                    Log.i(TAG, "signIn onSuccess: result $t")
                    if (t.nextStep.signInStep == AuthSignInStep.DONE) {
                        launch()
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            "invalid status:${t.nextStep.signInStep}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.e(TAG, "signIn onError: error ", e)
                    if (e is AuthException) {
                        val cause = e.cause
                        val message = if (cause is AmazonServiceException
                            && cause.errorMessage.isNotEmpty()
                        ) cause.errorMessage else e.message
                        Toast.makeText(
                            this@SignInActivity, message, Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    Toast.makeText(
                        this@SignInActivity,
                        "invalid status:${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun signUp(email: String, name: String, password: String) {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.name(), name)
            .build()
        DataRepository.getInstance(this)
            .amplifySignUp(email, password, options)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<AuthSignUpResult>(this) {
                override fun loading(): Boolean {
                    return true
                }

                override fun onSuccess(t: AuthSignUpResult) {
                    super.onSuccess(t)
                    Log.i(TAG, "signUp onSuccess: result $t")
                    if (t.nextStep.signUpStep == AuthSignUpStep.DONE) {
                        launch()
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            "invalid status:${t.nextStep.signUpStep}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    Log.e(TAG, "signUp onError: error ", e)
                    if (e is AuthException) {
                        val cause = e.cause
                        val message = if (cause is AmazonServiceException
                            && cause.errorMessage.isNotEmpty()
                        ) cause.errorMessage else e.message
                        Toast.makeText(
                            this@SignInActivity, message, Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    Toast.makeText(
                        this@SignInActivity,
                        "invalid status:${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun launch() {
        startActivity(Intent(this@SignInActivity, DeviceListActivity::class.java))
        finish()
    }
}