package io.omniedge

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.blankj.utilcode.util.Utils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import io.omniedge.n2n.N2NNotificationProvider
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created on 2019-12-17 11:24
 *
 */
class App : Application() {

    companion object {
        private const val TAG = "OmniEdgeApp"
        lateinit var instance: App
        val sp: SharedPreferences by lazy {
            instance.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        val enableSysTrace = enableSysTrace(BuildConfig.ENABLE_SYSTRACE)
        Log.d(TAG, "attachBaseContext: enableSysTrace=$enableSysTrace")
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        ThreadUtils.execute { sp.edit().apply() }

        Utils.init(this)
        initProvider()

        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        initRxJava()
        // Amplify Auth
        initAmplify()

        loadJNI()
        warmUpPingProcess()
    }

    private fun initRxJava() {
        RxJavaPlugins.setErrorHandler { Log.w(TAG, "Undeliverable exception received.", it) }
    }

    private fun initAmplify() {
        // TODO: 21-1-21 ANR reported, async init required
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            // disable amplify developer menu when device shake
            val configuration = AmplifyConfiguration.builder(this)
                    .devMenuEnabled(false)
                    .build()
            Amplify.configure(configuration, applicationContext)
            Log.i(TAG, "initAmplify: initialized amplify")
        } catch (error: AmplifyException) {
            Log.e(TAG, "initAmplify: could not initialize Amplify", error)
        }
    }

    private fun enableSysTrace(@Suppress("SameParameterValue") enable: Boolean): Boolean {
        if (!enable) {
            return false
        }
        return try {
            val trace = Class.forName("android.os.Trace")
            val setAppTracingAllowed = ApiHackHelper.getMethod(
                trace,
                "setAppTracingAllowed", Boolean::class.javaPrimitiveType
            )
            setAppTracingAllowed.invoke(null, true)
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    private fun initProvider() {
        ProviderManager.addProvider(N2NNotificationProvider.NAME, N2NNotificationProviderImpl())
    }

    private fun loadJNI() {
        // TODO: 2020/12/6 lazy load?
        System.loadLibrary("slog")
        System.loadLibrary("uip")
        System.loadLibrary("edge_v2")
        System.loadLibrary("edge_jni")
    }
}