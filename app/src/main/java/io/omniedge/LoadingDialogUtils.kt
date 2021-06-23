package io.omniedge

import android.content.Context
import android.os.Handler
import android.os.Looper
import io.omniedge.ui.LoadingDialog
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created on 2019-12-22 12:05
 *
 */
object LoadingDialogUtils {

    private val loading = AtomicBoolean(false)
    private val handler = Handler(Looper.getMainLooper())

    private var loadingDialog: LoadingDialog? = null

    fun showLoading(context: Context, cancelable: Boolean = true) {

        if (loading.compareAndSet(false, true)) {

            handler.post {
                loadingDialog?.dismiss()
                loadingDialog = LoadingDialog(context)
                loadingDialog?.setOnDismissListener {
                    loading.set(false)
                }
                loadingDialog?.setOnShowListener {
                    loading.set(true)
                }
                loadingDialog?.setCancelable(cancelable)
                loadingDialog?.setCanceledOnTouchOutside(cancelable)
                loadingDialog?.show()
            }


        }

    }

    fun hideLoading() {
        handler.post {
            loadingDialog?.dismiss()
            loadingDialog = null
        }

    }
}