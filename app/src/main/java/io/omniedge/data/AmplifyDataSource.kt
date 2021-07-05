package io.omniedge.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserStateDetails
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.api.graphql.model.ModelPagination
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.api.rest.RestResponse
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignOutOptions
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthWebUISignInOptions
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.options.AuthWebUISignInOptions
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.auth.result.AuthSignUpResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Device
import com.amplifyframework.datastore.generated.model.VirtualNetwork
import io.omniedge.*
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

class AmplifyDataSource {
    private companion object {
        private const val TAG = "AmplifyDataSource"
    }

    fun checkStatus(): Single<UserStateDetails> {
        return Single.create<UserStateDetails> { emitter ->
            emitter.onSuccess(AWSMobileClient.getInstance().currentUserState())
        }.subscribeOn(Schedulers.io())
    }

    fun signIn(email: String?, password: String?): Single<AuthSignInResult> {
        return Single.create<AuthSignInResult> { emitter ->
            Amplify.Auth.signIn(
                email, password,
                { emitter.onSuccess(it) },
                { emitter.onError(it) },
            )
        }.subscribeOn(Schedulers.io())
    }

    fun signInWithSocialAccount(
        provider: AuthProvider,
        callingActivity: Activity,
        signInOptions: AuthWebUISignInOptions?,
    ): Single<AuthSignInResult> {
        return Single.create<AuthSignInResult> { emitter ->
            val options = signInOptions
                ?: try {
                    AWSCognitoAuthWebUISignInOptions.builder()
                        .browserPackage(getDefaultBrowserPackageName(App.instance)).build()
                } catch (e: Exception) {
                    emitter.onError(e)
                    return@create
                }
            Amplify.Auth.signInWithSocialWebUI(
                provider,
                callingActivity,
                options,
                { emitter.onSuccess(it) },
                { emitter.onError(it) },
            )
        }.subscribeOn(Schedulers.io())
    }

    private fun getDefaultBrowserPackageName(context: Context): String =
        context.packageManager.resolveActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("http://")),
            PackageManager.MATCH_DEFAULT_ONLY
        )?.activityInfo?.packageName
            ?: throw RuntimeException(context.getString(R.string.browser_missing))

    fun signUp(
        email: String,
        password: String,
        options: AuthSignUpOptions,
    ): Single<AuthSignUpResult> {
        return Single.create<AuthSignUpResult> { emitter ->
            Amplify.Auth.signUp(
                email, password, options,
                { emitter.onSuccess(it) },
                { emitter.onError(it) },
            )
        }.subscribeOn(Schedulers.io())
    }

    fun signOut(signOutOptions: AuthSignOutOptions?): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            val options = signOutOptions
                ?: try {
                    AWSCognitoAuthSignOutOptions.builder()
                        .browserPackage(getDefaultBrowserPackageName(App.instance)).build()
                } catch (e: Exception) {
                    emitter.onError(e)
                    return@create
                }
            Amplify.Auth.signOut(
                options,
                { emitter.onSuccess(true) },
                { emitter.onError(it) },
            )
        }.subscribeOn(Schedulers.io())
    }

    private fun getIdToken(): String {
        val s = try {
            var result: String? = null
            val countDownLatch = CountDownLatch(1)
            Amplify.Auth.fetchAuthSession({
                if (it is AWSCognitoAuthSession) {
                    result = it.userPoolTokens.value?.idToken
                }
                countDownLatch.countDown()
            }, {
                countDownLatch.countDown()
            })
            countDownLatch.await()
            result
        } catch (e: Exception) {
            null
        }
        return s ?: ""
    }

    @Throws(IOException::class)
    private fun queryDevice(
        request: GraphQLRequest<PaginatedResult<Device?>>,
        result: MutableList<Device?>
    ) {
        val exceptionPrefix = "queryDevice exception "
        var ioException: IOException? = null
        val countDownLatch = CountDownLatch(1)
        val onResponse: (value: GraphQLResponse<PaginatedResult<Device?>>) -> Unit = { response ->
            if (response.hasData()) {
                result.addAll(response.data.items)
                if (response.data.hasNextResult()) {
                    queryDevice(response.data.requestForNextResult, result)
                }
            } else if (response.hasErrors()) {
                val exceptionDetail = response.errors.toTypedArray().contentToString()
                ioException = IOException("$exceptionPrefix $exceptionDetail")
            }
            countDownLatch.countDown()
        }
        val onFailure = { apiException: ApiException ->
            ioException = IOException(exceptionPrefix, apiException)
            countDownLatch.countDown()
        }
        Amplify.API.query(request, onResponse, onFailure)
        countDownLatch.await()
        val exp = ioException
        if (exp is IOException) {
            throw exp
        }
    }

    @Throws(IOException::class)
    private fun queryVirtualNetwork(
        request: GraphQLRequest<PaginatedResult<VirtualNetwork>>,
        result: MutableList<VirtualNetwork>
    ) {
        val exceptionPrefix = "queryVirtualNetwork exception "
        var ioException: IOException? = null
        val countDownLatch = CountDownLatch(1)
        val onResponse: (graphQLResponse: GraphQLResponse<PaginatedResult<VirtualNetwork>>) -> Unit =
            { response ->
                if (response.hasData()) {
                    result.addAll(response.data.items)
                    if (response.data.hasNextResult()) {
                        queryVirtualNetwork(response.data.requestForNextResult, result)
                    }
                } else if (response.hasErrors()) {
                    val exceptionDetail = response.errors.toTypedArray().contentToString()
                    ioException = IOException("$exceptionPrefix $exceptionDetail")
                }
                countDownLatch.countDown()
            }
        val onFailure = { apiException: ApiException ->
            ioException = IOException(exceptionPrefix, apiException)
            countDownLatch.countDown()
        }
        Amplify.API.query(request, onResponse, onFailure)
        countDownLatch.await()
        val exp = ioException
        if (exp is IOException) {
            throw exp
        }
    }

    // subscription not supported yet
    fun subscribeVirtualNetworks() {
/*
        val subscription: ApiOperation<*>? = Amplify.API.subscribe(
            ModelSubscription.onCreate(VirtualNetwork::class.java),
            { Log.i("ApiQuickStart", "Subscription established") },
            { onCreated ->
                Log.i(
                    "ApiQuickStart",
                    "Todo create subscription received: " + onCreated.data.name
                )
            },
            { onFailure -> Log.e("ApiQuickStart", "Subscription failed", onFailure) },
            { Log.i("ApiQuickStart", "Subscription completed") }
        )
*/
    }

    fun fetchDeviceListData(body: JSONObject): Single<DeviceListData> {
        return Single.create(object : SingleOnSubscribe<DeviceListData> {
            override fun subscribe(emitter: SingleEmitter<DeviceListData>) {
                try {
                    val virtualNetworks = mutableListOf<VirtualNetwork>()
                    queryVirtualNetwork(
                        ModelQuery.list(
                            VirtualNetwork::class.java,
                            ModelPagination.limit(3_000)
                        ), virtualNetworks
                    )
                    if (virtualNetworks.isEmpty()) {
                        throw IOException("empty virtualNetworks")
                    }
                    val virtualNetwork = virtualNetworks[0]

                    val joinVirtualNetworkResponse: JoinVirtualNetworkResponse =
                        joinVirtualNetwork(body, virtualNetwork)

                    val deviceList = mutableListOf<Device?>()
                    queryDevice(
                        ModelQuery.list(
                            Device::class.java,
                            ModelPagination.firstPage().withLimit(1_000)
                        ), deviceList
                    )
                    val deviceListData =
                        DeviceListData(virtualNetwork, joinVirtualNetworkResponse, deviceList)
                    emitter.onSuccess(deviceListData)
                } catch (e: Exception) {
                    emitter.onError(e)
                }
            }
        })
    }

    @Throws(IOException::class)
    private fun joinVirtualNetwork(
        body: JSONObject,
        virtualNetwork: VirtualNetwork
    ): JoinVirtualNetworkResponse {
        var ioException: IOException? = null
        val countDownLatch = CountDownLatch(1)
        var joinVirtualNetworkResponse: JoinVirtualNetworkResponse? = null
        body.put("virtualNetworkID", virtualNetwork.id)
        val onResponse: (value: RestResponse) -> Unit = { response ->
            try {
                joinVirtualNetworkResponse =
                    response.data.asJSONObject().toJoinVirtualNetworkResponse()
            } catch (e: Exception) {
                ioException = IOException("joinVirtualNetwork failed ${response.data.asString()}")
            }
            countDownLatch.countDown()
        }
        val onFailure: (value: ApiException) -> Unit = {
            ioException = IOException("joinVirtualNetwork failed")
            countDownLatch.countDown()
        }
        Amplify.API.post(
            RestOptions.builder()
                .addPath("/virtual-network/${virtualNetwork.id}/join")
                .addHeader("Authorization", getIdToken())
                .addHeader("content-type", "application/json")
                .addBody(body.toString().toByteArray())
                .build(),
            onResponse,
            onFailure
        )

        countDownLatch.await()
        if (!joinVirtualNetworkResponse.isValid()) {
            ioException = IOException("joinVirtualNetwork failed res=null")
        }
        val exp = ioException
        if (exp is IOException) {
            throw exp
        }
        return joinVirtualNetworkResponse!!
    }
}

