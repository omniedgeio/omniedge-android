package io.omniedge

import android.content.Context
import io.reactivex.disposables.CompositeDisposable

/**
 * Created on 2019-12-22 11:52
 *
 */
interface PageView {

    fun getCurContext(): Context

    fun compositeDisposable(): CompositeDisposable

    fun showToast(msg: String)

}