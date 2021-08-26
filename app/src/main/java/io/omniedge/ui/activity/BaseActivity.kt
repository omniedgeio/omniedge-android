package io.omniedge.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.omniedge.PageView
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.include_toolbar.*

/**
 * Base Activity
 * Created on 2019-12-17 13:36
 *
 */
@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity(), PageView {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @LayoutRes
    protected open fun getLayoutRes(): Int {
        return -1
    }

    protected open fun getLayoutView(): View? {
        return null
    }

    @StringRes
    protected open fun getPageTitle(): Int = 0

    @CallSuper
    open fun init() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun toast(@StringRes msg: Int) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun toast(msg: String) {
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutRes = getLayoutRes()
        if (layoutRes > 0) {
            setContentView(layoutRes)
        } else {
            val layoutView = getLayoutView()
            if (layoutView != null) {
                setContentView(layoutView)
            }
        }

        if (toolbar != null) {
            val pageTitle = getPageTitle()
            if (pageTitle > 0) {
                text_title?.text = getString(pageTitle)
            }
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(showBack())
        }

        init()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        super.onDestroy()
    }

    override fun getCurContext(): Context {
        return this
    }

    override fun compositeDisposable(): CompositeDisposable {
        return compositeDisposable
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    open fun showBack(): Boolean {
        return true
    }

}

fun Context.launch(cls: Class<*>, finish: Boolean = false) {
    startActivity(Intent(this, cls))
    if (finish) {
        if (this is Activity) {
            finish()
        } else if (this is Fragment) {
            requireActivity().finish()
        }
    }
}