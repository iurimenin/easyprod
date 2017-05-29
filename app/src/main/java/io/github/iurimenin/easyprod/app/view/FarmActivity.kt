package io.github.iurimenin.easyprod.app.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.ui.auth.AuthUI
import com.rengwuxian.materialedittext.MaterialEditText
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.model.FarmModel
import io.github.iurimenin.easyprod.app.presenter.FarmPresenter
import io.github.iurimenin.easyprod.app.util.CallbackInterface
import kotlinx.android.synthetic.main.activity_farms.*

/**
 * Created by Iuri Menin on 18/05/17.
 */
class FarmActivity : AppCompatActivity(), CallbackInterface {

    private var mPresenter: FarmPresenter? = null
    private var mMenuItemAbout: MenuItem? = null
    private var mMenuItemLogout: MenuItem? = null
    private var mMenuItemDelete: MenuItem? = null
    private var mMenuItemEdit: MenuItem? = null

    private val mAdapter: FarmAdapter = FarmAdapter(this, ArrayList<FarmModel>()) {
        mPresenter?.clickItem(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farms)

        floatingActionButtonAddFarm.setOnClickListener({ addFarm() })

        superRecyclerViewFarms.setLayoutManager(LinearLayoutManager(this))
        superRecyclerViewFarms.adapter = mAdapter

        if (mPresenter == null)
            mPresenter = FarmPresenter()

        mPresenter?.bindView(this, mAdapter)
        mPresenter?.loadFarms()
        this.updateMenuIcons()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.unBindView()
        mPresenter = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_farm, menu)

        mMenuItemAbout = menu.findItem(R.id.menuMainItemAbout)
        mMenuItemLogout = menu.findItem(R.id.menuMainItemLogout)
        mMenuItemDelete = menu.findItem(R.id.menuMainItemDelete)
        mMenuItemEdit = menu.findItem(R.id.menuMainItemEdit)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menuMainItemLogout -> this.logout()

            R.id.menuMainItemEdit -> this.updateFarm()

            R.id.menuMainItemDelete -> mPresenter?.deleteSelectedItems(mAdapter.selectedItens)
        }
        return true
    }

    override fun updateMenuIcons() {

        when (mAdapter.selectedItens.size) {

            0 -> {
                mMenuItemDelete?.isVisible = false
                mMenuItemEdit?.isVisible = false
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

    private fun addFarm() {
        MaterialDialog.Builder(this)
                .title(R.string.new_farm)
                .titleColorRes(R.color.colorPrimary)
                .contentColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
                .negativeColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .customView(R.layout.new_farm_view, true)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_cancel)
                .autoDismiss(false)
                .onAny { materialDialog, dialogAction ->
                    mPresenter?.saveFarm(materialDialog, dialogAction == DialogAction.POSITIVE) }
                .show()
    }

    private fun updateFarm() {
        //We can select index 0 because the edit item will only be visible
        // when only 1 item is selected
        val farm = mAdapter.selectedItens[0]

        val builder = MaterialDialog.Builder(this)
                .title(R.string.new_farm)
                .titleColorRes(R.color.colorPrimary)
                .contentColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
                .negativeColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .customView(R.layout.new_farm_view, true)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_cancel)
                .autoDismiss(false)
                .onAny { materialDialog, dialogAction ->
                    mPresenter?.saveFarm(materialDialog, dialogAction == DialogAction.POSITIVE) }

        val textViewFarmKey = builder.build().findViewById(R.id.textViewFarmKey) as TextView
        textViewFarmKey.text = farm.key
        val materialEditTextFarmName =
                builder.build().findViewById(R.id.materialEditTextFarmName) as MaterialEditText
        materialEditTextFarmName.setText(farm.name)

        builder.show()
    }

    private fun logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    // user is now signed out
                    this.startActivity(Intent(this, LoginActivity::class.java))
                    this.finish()
                }
    }
}
