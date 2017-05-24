package io.github.iurimenin.easyprod.farm.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.ui.auth.AuthUI
import com.rengwuxian.materialedittext.MaterialEditText
import io.github.iurimenin.easyprod.CallbackInterface
import io.github.iurimenin.easyprod.LoginActivity
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.area.view.AreaActivity
import io.github.iurimenin.easyprod.farm.model.FarmModel
import io.github.iurimenin.easyprod.farm.presenter.FarmPresenter
import io.github.iurimenin.easyprod.farm.util.FarmAdapter
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Iuri Menin on 18/05/17.
 */
class FarmActivity : AppCompatActivity(), CallbackInterface {

    override fun executeCallback() {
        updateMenuIcons()
    }

    private var mPresenter: FarmPresenter? = null
    private var mMenuItemAbout: MenuItem? = null
    private var mMenuItemLogout: MenuItem? = null
    private var mMenuItemDelete: MenuItem? = null
    private var mMenuItemEdit: MenuItem? = null

    private val mAdapter: FarmAdapter = FarmAdapter(this, ArrayList<FarmModel>()) {
        clickItem(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        menuInflater.inflate(R.menu.menu_main, menu)

        mMenuItemAbout = menu.findItem(R.id.menuItemAbout);
        mMenuItemLogout = menu.findItem(R.id.menuItemLogout);
        mMenuItemDelete = menu.findItem(R.id.menuItemDelete);
        mMenuItemEdit = menu.findItem(R.id.menuItemEdit);

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menuItemLogout -> logout()

            R.id.menuItemEdit -> updateFarm()

            R.id.menuItemDelete -> mPresenter?.deleteSelectedItems(mAdapter.selectedItens)
        }
        return true
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

    private fun logout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    // user is now signed out
                    startActivity(Intent(this@FarmActivity, LoginActivity::class.java))
                    finish()
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
                .onAny { materialDialog, dialogAction ->  saveFarm(materialDialog, dialogAction) }
                .show()
    }

    private fun clickItem (farmModel: FarmModel) {

        val i = Intent(this, AreaActivity::class.java)
        i.putExtra(FarmModel.TAG, farmModel)
        startActivity(i)
        finish()
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
                .onAny { materialDialog, dialogAction ->  saveFarm(materialDialog, dialogAction) }

        val textViewFarmKey = builder.build().findViewById(R.id.textViewFarmKey) as TextView
        textViewFarmKey.text = farm.key
        val materialEditTextFarmName =
                builder.build().findViewById(R.id.materialEditTextFarmName) as MaterialEditText
        materialEditTextFarmName.setText(farm.name)

        builder.show()
    }

    private fun saveFarm(materialDialog: MaterialDialog, dialogAction: DialogAction) {

        if (dialogAction == DialogAction.POSITIVE) {

            val editTextFarmName: EditText =
                    materialDialog.findViewById(R.id.materialEditTextFarmName) as EditText

            val textViewFarmKey: TextView =
                    materialDialog.findViewById(R.id.textViewFarmKey) as TextView

            val farmName = editTextFarmName.text.toString()
            if (farmName.isNullOrEmpty())
                editTextFarmName.error = getString(R.string.error_field_required)
            else {

                val farm = FarmModel(textViewFarmKey.text.toString(), farmName)
                farm.save()
                materialDialog.dismiss()
            }
        } else {
            materialDialog.dismiss()
        }
    }
}
