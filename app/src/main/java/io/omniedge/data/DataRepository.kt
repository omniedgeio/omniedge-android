package io.omniedge.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.options.AuthWebUISignInOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import io.omniedge.DeviceListData
import io.omniedge.SingletonHolder
import io.reactivex.Single
import org.json.JSONObject

/**
 * Created on 2019-12-22 11:24
 *
 */
class DataRepository private constructor(private val context: Context) {

    private val amplifyDataSource = AmplifyDataSource()

    companion object {
        private const val TAG = "DataRepository"
        private val instance = SingletonHolder<DataRepository, Context> {
            return@SingletonHolder DataRepository(it)
        }

        fun getInstance(context: Context): DataRepository {
            return instance.getInstance(context.applicationContext)
        }

    }

    fun checkAmplifyStatus(): Single<UserStateDetails> {
        return amplifyDataSource.checkStatus()
            .doOnSuccess {
                Log.d(TAG, "check amplify status result:$it")
            }
    }

    fun amplifySignIn(email: String?, password: String?): Single<AuthSignInResult> {
        return amplifyDataSource.signIn(email, password)
            .doOnSuccess {
                Log.d(TAG, "sign in result:$it")
            }
    }

    fun amplifySignInWithSocialAccount(
        provider: AuthProvider,
        callingActivity: Activity,
        options: AuthWebUISignInOptions? = null,
    ): Single<AuthSignInResult> {
        return amplifyDataSource.signInWithSocialAccount(provider, callingActivity, options)
            .doOnSuccess {
                Log.d(TAG, "sign in with social result:$it")
            }
    }

    fun amplifySignUp(
        email: String,
        password: String,
        options: AuthSignUpOptions = AuthSignUpOptions.builder().build()
    ): Single<AuthSignUpResult> {
        return amplifyDataSource.signUp(email, password, options)
            .doOnSuccess {
                Log.d(TAG, "sign up result:$it")
            }
    }

    fun amplifySignOut(
        options: AuthSignOutOptions? = null
    ): Single<Boolean> {
        return amplifyDataSource.signOut(options)
            .doOnSuccess {
                Log.d(TAG, "sign out result:$it")
            }
    }

    fun fetchDeviceListData(body: JSONObject): Single<DeviceListData> {
        return amplifyDataSource.fetchDeviceListData(body)
    }


}