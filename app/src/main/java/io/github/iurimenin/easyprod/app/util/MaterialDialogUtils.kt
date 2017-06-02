package io.github.iurimenin.easyprod.app.util

import android.content.Context
import android.support.v4.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import io.github.iurimenin.easyprod.R

/**
 * Created by Iuri Menin on 02/06/17.
 */
class MaterialDialogUtils(var context : Context) {

    fun getDialog(viewId: Int, title: Int): MaterialDialog.Builder? {
        return MaterialDialog.Builder(context)
                .title(title)
                .titleColorRes(R.color.colorPrimary)
                .contentColor(ContextCompat.getColor(context, R.color.colorPrimaryText))
                .negativeColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                .positiveColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .customView(viewId, true)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_cancel)
                .autoDismiss(false)
    }
}

