package io.omniedge

import androidx.annotation.Keep
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created on 2020/8/7  11:15
 * chenpan
 */
@Suppress("UNUSED")
@Keep
class RxBus {

    companion object{
        val INSTANCE = Holder.INSTANCE
        private val mPublishSubject= PublishSubject.create<Any>().toSerialized()
    }

    fun post(o: Any) {
        mPublishSubject.onNext(o)
    }

    fun <T> toObservable(clazz: Class<T>): Observable<T> {
        return mPublishSubject.ofType(clazz)
    }

    private class Holder {
        companion object {
            val INSTANCE: RxBus = RxBus()
        }
    }
}