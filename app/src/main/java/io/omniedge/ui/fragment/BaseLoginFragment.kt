package io.omniedge.ui.fragment

import android.content.Context
import io.omniedge.*
import io.omniedge.data.bean.LoginResponse
import io.omniedge.ui.activity.DeviceListActivity
import io.omniedge.ui.activity.launch
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.HttpException

abstract class BaseLoginFragment : BaseFragment() {
    companion object {
        private const val TAG = "BaseLoginFragment"
    }

    protected fun signIn(email: String, password: String) {
        App.repository
            .login(email, password)
            .handleLoginResult(this, requireContext())
    }
}

fun Single<LoginResponse>.handleLoginResult(pageView: PageView, context: Context) {
    this.doOnSuccess {
        App.repository.updateToken(it.data?.token)
    }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : BusObserver<LoginResponse>(pageView) {
            override fun loading(): Boolean {
                return true
            }

            override fun onSuccess(t: LoginResponse) {
                super.onSuccess(t)
                OmniLog.i("signIn onSuccess: result ${t.data?.token}")
                if (t.data?.token.isNullOrBlank()) {
                    pageView.showToast(context.getString(R.string.invalid_token))
                    return
                }
                context.launch(DeviceListActivity::class.java, true)
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                if (e is HttpException) {
                    App.repository.updateToken(null)
                }
            }
        })
}

