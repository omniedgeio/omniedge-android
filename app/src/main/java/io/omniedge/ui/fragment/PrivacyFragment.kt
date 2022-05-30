package io.omniedge.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import im.delight.android.webview.AdvancedWebView
import io.omniedge.R


/**
 * Created on 2020-12-06
 * 使用条款
 */
@Suppress("RemoveEmptyPrimaryConstructor")
class PrivacyFragment() : BaseFragment(), AdvancedWebView.Listener {
    companion object {
        private const val PRIVATE_URL = "http://www.example.org/"
    }

    private lateinit var mWebView: AdvancedWebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_webview, container, false)
        mWebView = rootView.findViewById(R.id.webview)
        mWebView.setListener(activity, this)
        mWebView.setMixedContentAllowed(false)
        mWebView.loadUrl(PRIVATE_URL)
        val tvTitle: TextView = rootView.findViewById(R.id.text_title)
        tvTitle.text = getString(R.string.privacy_title)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        mWebView.onResume()
    }

    override fun onPause() {
        mWebView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mWebView.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        mWebView.onActivityResult(requestCode, resultCode, intent)
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {}

    override fun onPageFinished(url: String?) {}

    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {}

    override fun onDownloadRequested(
        url: String?, suggestedFilename: String?, mimeType: String?,
        contentLength: Long, contentDisposition: String?, userAgent: String?
    ) {
    }

    override fun onExternalPageRequest(url: String?) {

    }
}