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
import io.github.iurimenin.easyprod.app.model.FieldModel
import io.github.iurimenin.easyprod.app.presenter.FieldPresenter
import io.github.iurimenin.easyprod.app.util.CallbackInterface
import kotlinx.android.synthetic.main.activity_fields.*

class FieldActivity : AppCompatActivity(), CallbackInterface {

    private var mPresenter: FieldPresenter? = null
    private var mMenuItemDelete: MenuItem? = null
    private var mMenuItemEdit: MenuItem? = null
    private var mFarm: FarmModel? = null

    private val mAdapter: FieldAdapter = FieldAdapter(this, ArrayList<FieldModel>()) {
        mPresenter?.clickItem(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fields)

        mFarm = intent.extras[FarmModel.TAG] as FarmModel

        textViewFarmName.text = mFarm?.name

        floatingActionButtonAddField.setOnClickListener { addFiled() }

        superRecyclerViewFields.setLayoutManager(LinearLayoutManager(this))
        superRecyclerViewFields.adapter = mAdapter

        if (mPresenter == null)
            mPresenter = FieldPresenter(mFarm?.key)

        mPresenter?.bindView(this)
        mPresenter?.loadFields()
        updateMenuIcons()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.unBindView()
        mPresenter = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_field, menu)

        mMenuItemDelete = menu.findItem(R.id.menuFieldItemDelete);
        mMenuItemEdit = menu.findItem(R.id.menuFieldItemEdit);

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menuFieldItemEdit -> updateField()

            R.id.menuFieldItemDelete -> mPresenter?.deleteSelectedItems(mAdapter.selectedItens)
        }
        return true
    }

    override fun executeCallback() {
        updateMenuIcons()
    }

    fun addItem(vo: FieldModel) {
        mAdapter.addItem(vo)
    }

    fun updateItem(updated: FieldModel) {
        mAdapter.updateItem(updated)
    }

    fun removeItem(removed: FieldModel) {
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
            }

            1 -> {
                mMenuItemDelete?.setVisible(true)
                mMenuItemEdit?.setVisible(true)
            }

            !in(0..1) -> {
                mMenuItemDelete?.setVisible(true)
                mMenuItemEdit?.setVisible(false)
            }
        }
    }

    private fun addFiled() {
        MaterialDialog.Builder(this)
                .title(R.string.new_field)
                .titleColorRes(R.color.colorPrimary)
                .contentColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
                .negativeColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .customView(R.layout.new_field_view, true)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_cancel)
                .autoDismiss(false)
                .onAny { materialDialog, dialogAction ->
                    mPresenter?.saveField(materialDialog, dialogAction == DialogAction.POSITIVE) }
                .show()
    }

    private fun updateField() {
        //We can select index 0 because the edit item will only be visible
        // when only 1 item is selected
        val field = mAdapter.selectedItens[0]

        val builder = MaterialDialog.Builder(this)
                .title(R.string.new_field)
                .titleColorRes(R.color.colorPrimary)
                .contentColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
                .negativeColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .customView(R.layout.new_field_view, true)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_cancel)
                .autoDismiss(false)
                .onAny { materialDialog, dialogAction ->
                    mPresenter?.saveField(materialDialog, dialogAction == DialogAction.POSITIVE) }

        val textViewFieldKey = builder.build().findViewById(R.id.textViewFieldKey) as TextView
        textViewFieldKey.text = field.key
        val materialEditTextFieldName =
                builder.build().findViewById(R.id.materialEditTextFieldName) as MaterialEditText
        materialEditTextFieldName.setText(field.name)

        builder.show()
    }
}
