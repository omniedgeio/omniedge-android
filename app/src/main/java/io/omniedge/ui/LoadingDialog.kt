package io.omniedge.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import io.omniedge.R

/**
 * Created on 2019-12-22 12:00
 *
 */
class LoadingDialog(context: Context) : AppCompatDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
    }

}