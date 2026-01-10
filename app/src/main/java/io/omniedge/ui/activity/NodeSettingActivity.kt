package io.omniedge.ui.activity

import io.omniedge.R
// Removed synthetic import

/**
 * Created on 2019-12-22 13:26
 *
 */
class NodeSettingActivity : BaseActivity() {

    override fun getPageTitle(): Int {
        return R.string.node_setting
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_node_setting
    }

    override fun init() {
        super.init()
        findViewById<android.view.View>(R.id.btn_connect).setOnClickListener {
            finish()
        }
    }
}