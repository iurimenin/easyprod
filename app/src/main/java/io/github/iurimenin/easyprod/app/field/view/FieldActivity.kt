package io.github.iurimenin.easyprod.app.field.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.rengwuxian.materialedittext.MaterialEditText
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.farm.model.FarmModel
import io.github.iurimenin.easyprod.app.field.model.FieldModel
import io.github.iurimenin.easyprod.app.field.presenter.FieldPresenter
import io.github.iurimenin.easyprod.app.util.MaterialDialogUtils
import io.github.iurimenin.easyprod.app.util.MoneyMaskMaterialEditText
import io.github.iurimenin.easyprod.app.view.EasyProdActitivty
import kotlinx.android.synthetic.main.activity_fields.*

class FieldActivity : EasyProdActitivty() {

    private var mFarm: FarmModel? = null
    private var mPresenter: FieldPresenter? = null
    private var mMaterialDialogUtils : MaterialDialogUtils? = null

    private val mAdapter: FieldAdapter = FieldAdapter(this, ArrayList<FieldModel>()) {
        mPresenter?.clickItem(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fields)

        val arrowBack = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp)
        arrowBack.setColorFilter(ContextCompat.getColor(this, android.R.color.white),
                PorterDuff.Mode.SRC_ATOP)

        supportActionBar?.apply {
            title = getString(R.string.fiels)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(arrowBack)
        }

        mFarm = intent.extras[FarmModel.TAG] as FarmModel

        textViewFarm.text = mFarm?.name

        floatingActionButtonAddField.setOnClickListener { addFiled() }

        superRecyclerViewFields.setLayoutManager(LinearLayoutManager(this))
        superRecyclerViewFields.adapter = mAdapter

        if (mPresenter == null)
            mPresenter = FieldPresenter(mFarm?.key)

        if (mMaterialDialogUtils == null)
            mMaterialDialogUtils = MaterialDialogUtils(this)

        mPresenter?.bindView(this, mAdapter)
        mPresenter?.loadFields()
        updateMenuIcons(mAdapter.itemCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.unBindView()
        mPresenter = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_field, menu)

        mMenuItemDelete = menu.findItem(R.id.menuFieldItemDelete)
        mMenuItemEdit = menu.findItem(R.id.menuFieldItemEdit)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            android.R.id.home -> onBackPressed()

            R.id.menuFieldItemEdit -> updateField()

            R.id.menuFieldItemDelete -> mPresenter?.deleteSelectedItems(mAdapter.selectedItens)
        }
        return true
    }

    private fun addFiled() {
        mMaterialDialogUtils?.getDialog(R.layout.new_field_view, R.string.new_field, mPresenter)?.show()
    }

    private fun updateField() {
        //We can select index 0 because the edit item will only be visible
        // when only 1 item is selected
        val field = mAdapter.selectedItens[0]
        val builder = mMaterialDialogUtils?.getDialog(R.layout.new_field_view, R.string.field, mPresenter)

        val textViewFieldKey = builder?.build()?.findViewById(R.id.textViewFieldKey) as TextView
        textViewFieldKey.text = field.key

        val materialEditTextFieldName =
                builder.build().findViewById(R.id.materialEditTextFieldName) as MaterialEditText
        materialEditTextFieldName.setText(field.name)

        val materialEditTextFieldArea: MoneyMaskMaterialEditText =
                builder.build().findViewById(R.id.materialEditTextFieldArea) as MoneyMaskMaterialEditText
        materialEditTextFieldArea.setTextFromDouble(field.totalArea)

        builder.show()
    }
}
