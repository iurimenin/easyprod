package io.github.iurimenin.easyprod.app.view

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
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

        mPresenter?.bindView(this)
        mPresenter?.loadFarms()
        updateMenuIcons()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.unBindView()
        mPresenter = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_farm, menu)

        mMenuItemAbout = menu.findItem(R.id.menuMainItemAbout);
        mMenuItemLogout = menu.findItem(R.id.menuMainItemLogout);
        mMenuItemDelete = menu.findItem(R.id.menuMainItemDelete);
        mMenuItemEdit = menu.findItem(R.id.menuMainItemEdit);

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menuMainItemLogout -> mPresenter?.logout()

            R.id.menuMainItemEdit -> updateFarm()

            R.id.menuMainItemDelete -> mPresenter?.deleteSelectedItems(mAdapter.selectedItens)
        }
        return true
    }

    override fun executeCallback() {
        updateMenuIcons()
    }

    fun addItem(vo: FarmModel) {
        mAdapter.addItem(vo)
    }

    fun updateItem(updated: FarmModel) {
        mAdapter.updateItem(updated)
    }

    fun removeItem(removed: FarmModel) {
        mAdapter.removeItem(removed)
        updateMenuIcons()
    }

    fun removeSelecionts () {
        mAdapter.removeSelecionts()
    }

    private fun updateMenuIcons() {

        when (mAdapter.selectedItens.size) {

            0 -> {
                mMenuItemDelete?.setVisible(false)
                mMenuItemEdit?.setVisible(false)
                mMenuItemAbout?.setVisible(true)
                mMenuItemLogout?.setVisible(true)
            }

            1 -> {
                mMenuItemDelete?.setVisible(true)
                mMenuItemEdit?.setVisible(true)
                mMenuItemAbout?.setVisible(false)
                mMenuItemLogout?.setVisible(false)
            }

            !in(0..1) -> {
                mMenuItemDelete?.setVisible(true)
                mMenuItemEdit?.setVisible(false)
                mMenuItemAbout?.setVisible(false)
                mMenuItemLogout?.setVisible(false)
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
}
