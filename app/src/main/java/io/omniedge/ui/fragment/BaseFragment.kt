package io.omniedge.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.omniedge.PageView
import io.reactivex.disposables.CompositeDisposable

/**
 * Created on 2019-12-17 15:35
 *
 */
abstract class BaseFragment : Fragment(), PageView {

    private val compositeDisposable = CompositeDisposable()

    fun toast(view: View, @StringRes msg: Int) {
        activity?.runOnUiThread {
            Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .apply {
                    this.view
                        .findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                        .maxLines = 5
                }
                .show()
        }
    }

    private fun toast(view: View, msg: String) {
        activity?.runOnUiThread {
            Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .apply {
                    this.view
                        .findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                        .maxLines = 5
                }
                .show()
        }
    }

    @LayoutRes
    protected open fun getLayoutRes(): Int {
        return -1
    }

    protected open fun getLayoutView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
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

        val layoutRes = getLayoutRes()
        if (layoutRes > 0) {
            return inflater.inflate(layoutRes, container, false)
        } else {
            val layoutView = getLayoutView(inflater, container, savedInstanceState)
            if (layoutView != null) {
                return layoutView
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getCurContext(): Context {
        return requireContext()
    }

    override fun compositeDisposable(): CompositeDisposable {
        return compositeDisposable
    }

    override fun showToast(msg: String) {
        toast(requireActivity().findViewById(android.R.id.content), msg)
    }
}
