package io.github.iurimenin.easyprod.app.view

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import io.github.iurimenin.easyprod.app.util.CallbackInterface

/**
 * Created by Iuri Menin on 05/06/17.
 */
abstract class EasyProdActitivty : AppCompatActivity(), CallbackInterface {

    protected var mMenuItemEdit: MenuItem? = null
    protected var mMenuItemDelete: MenuItem? = null

    protected var mMenuItemAbout: MenuItem? = null
    protected var mMenuItemLogout: MenuItem? = null

    override fun updateMenuIcons(itensCount : Int?) {
        when (itensCount) {

            0 -> {
                mMenuItemEdit?.isVisible = false
                mMenuItemDelete?.isVisible = false
                mMenuItemAbout?.isVisible = true
                mMenuItemLogout?.isVisible = true
            }

            1 -> {
                mMenuItemDelete?.isVisible = true
                mMenuItemEdit?.isVisible = true
                mMenuItemAbout?.isVisible = false
                mMenuItemLogout?.isVisible = false
            }

            !in(0..1) -> {
                mMenuItemDelete?.isVisible = true
                mMenuItemEdit?.isVisible = false
                mMenuItemAbout?.isVisible = false
                mMenuItemLogout?.isVisible = false
            }
        }
    }
}