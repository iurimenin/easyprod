package io.github.iurimenin.easyprod.app.util

import com.afollestad.materialdialogs.MaterialDialog

/**
 * Created by Iuri Menin on 05/06/17.
 */
interface PresenterInterface {

    fun save(materialDialog: MaterialDialog, isPositive: Boolean)
}