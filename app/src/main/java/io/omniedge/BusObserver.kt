package io.omniedge

import android.app.Activity
import android.content.pm.ActivityInfo
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created on 2019-12-22 11:50
 *
 */
open class BusObserver<T>(private val pageView: PageView) :
    SingleObserver<T>, CompletableObserver, Observer<T> {

    override fun onNext(t: T) {
        setScreenOrientationLock(false)
        LoadingDialogUtils.hideLoading()
    }

    open fun loading(): Boolean {
        return true
    }

    open fun loadingCancelable(): Boolean {
        return false
    }

    override fun onComplete() {
        setScreenOrientationLock(false)
        LoadingDialogUtils.hideLoading()
    }

    override fun onSuccess(t: T) {
        setScreenOrientationLock(false)
        LoadingDialogUtils.hideLoading()
    }

    override fun onSubscribe(d: Disposable) {
        pageView.compositeDisposable().add(d)
        setScreenOrientationLock(true)
        if (loading()) {
            LoadingDialogUtils.showLoading(pageView.getCurContext(), loadingCancelable())
        }
    }

    private fun setScreenOrientationLock(locked: Boolean) {
        val context = pageView.getCurContext()
        if (context is Activity) {
            context.requestedOrientation = if (locked) {
                ActivityInfo.SCREEN_ORIENTATION_LOCKED
            } else {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }
        }
    }

    override fun onError(e: Throwable) {
        setScreenOrientationLock(false)
        LoadingDialogUtils.hideLoading()
        e.printStackTrace()
        val errorMsg = e.message
        if (errorMsg != null) {
            pageView.showToast(errorMsg)
        }
    }
}