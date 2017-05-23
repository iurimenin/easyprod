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
import io.github.iurimenin.easyprod.LoginActivity
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.farm.model.FarmModel
import io.github.iurimenin.easyprod.farm.presenter.FarmPresenter
import io.github.iurimenin.easyprod.farm.util.FarmAdapter
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Iuri Menin on 18/05/17.
 */
class FarmActivity : AppCompatActivity() {

    private var presenter: FarmPresenter? = null

    val mAdapter: FarmAdapter = FarmAdapter(ArrayList<FarmModel>()) {
        updateFarm(it.key, it.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        floatingActionButtonAddFarm.setOnClickListener({ addFarm() })

        superRecyclerViewFarms.setLayoutManager(LinearLayoutManager(this))
        superRecyclerViewFarms.adapter = mAdapter

        if (presenter == null)
            presenter = FarmPresenter()

        presenter?.bindView(this)
        presenter?.loadFarms()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.unBindView()
        presenter = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menuItemLogout -> logout()
        }
        return true
    }

    fun  addItem(vo: FarmModel) {
        mAdapter.addItem(vo)
    }

    fun  updateItem(updated: FarmModel) {
        mAdapter.updateItem(updated)
    }

    fun  removeItem(removed: FarmModel) {
        mAdapter.removeItem(removed)
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
                .customView(R.layout.new_farm_view, true)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_cancel)
                .autoDismiss(false)
                .onAny { materialDialog, dialogAction ->  saveFarm(materialDialog, dialogAction) }
                .show()
    }

    private fun updateFarm(key : String, name :String) {
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
        textViewFarmKey.text = key
        val materialEditTextFarmName =
                builder.build().findViewById(R.id.materialEditTextFarmName) as MaterialEditText
        materialEditTextFarmName.setText(name)

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
