package io.omniedge.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import io.omniedge.PageView
import io.reactivex.disposables.CompositeDisposable

/**
 * Created on 2019-12-17 15:35
 *
 */
abstract class BaseFragment : Fragment(), PageView {

    private val compositeDisposable = CompositeDisposable()

    fun toast(@StringRes msg: Int) {
        activity?.runOnUiThread {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun toast(msg: String) {
        activity?.runOnUiThread {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    @LayoutRes
    open fun getLayoutRes(): Int {
        return -1
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (getLayoutRes() > 0) {
            return inflater.inflate(getLayoutRes(), container, false)
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getCurContext(): Context {
        return context!!
    }

    override fun compositeDisposable(): CompositeDisposable {
        return compositeDisposable
    }

    override fun showToast(msg: String) {
        toast(msg)
    }
}
