package io.omniedge.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.core.Amplify
import io.omniedge.BusObserver
import io.omniedge.DeviceListActivity
import io.omniedge.data.DataRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * launcher
 * Created on 2019-12-17 13:03
 *
 */
class LauncherActivity : BaseActivity() {

    private companion object {
        private const val TAG = "SignInActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAmplifyState()
    }

    private fun checkAmplifyState() {
        val currentUser = Amplify.Auth.currentUser
        Log.d(TAG, "prepareAmplifySignIn: current user:$currentUser")

        DataRepository.getInstance(this)
            .checkAmplifyStatus()
            .observeOn(Schedulers.io())
            .doOnSuccess { statusDetails ->
                if (AWSMobileClient.getInstance().isSignedIn
                    && statusDetails.userState == UserState.SIGNED_IN
                ) {
                    startActivity(Intent(this, DeviceListActivity::class.java))
                    finish()
                    return@doOnSuccess
                }
                launchSignIn()
            }
            .doOnError { launchSignIn() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : BusObserver<UserStateDetails>(this) {
                override fun loading(): Boolean {
                    return false
                }
            })
    }

    private fun launchSignIn() {
        startActivity(Intent(this@LauncherActivity, SignInActivity::class.java))
        finish()
    }
}