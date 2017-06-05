package io.github.iurimenin.easyprod.app.production.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.cultivation.model.CultivationModel
import io.github.iurimenin.easyprod.app.field.model.FieldModel
import io.github.iurimenin.easyprod.app.production.model.ProductionModel
import io.github.iurimenin.easyprod.app.production.presenter.ProductionPresenter
import io.github.iurimenin.easyprod.app.util.MaterialDialogUtils
import io.github.iurimenin.easyprod.app.util.MoneyMaskMaterialEditText
import io.github.iurimenin.easyprod.app.util.ProductionInterface
import kotlinx.android.synthetic.main.activity_production.*
import java.text.NumberFormat

class ProductionActivity : AppCompatActivity(), ProductionInterface {

    private var mCultivation: CultivationModel? = null
    private var mField: FieldModel? = null
    private var mMenuItemEdit: MenuItem? = null
    private var mMenuItemDelete: MenuItem? = null
    private var mPresenter: ProductionPresenter? = null
    private var mMaterialDialogUtils : MaterialDialogUtils? = null

    private val mAdapter: ProductionAdapter = ProductionAdapter(this, ArrayList<ProductionModel>()) {
        mPresenter?.clickItem(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_production)

        val arrowBack = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp)
        arrowBack.setColorFilter(ContextCompat.getColor(this, android.R.color.white),
                PorterDuff.Mode.SRC_ATOP)

        supportActionBar?.apply {
            title = getString(R.string.fiels)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(arrowBack)
        }

        mCultivation = intent.extras[CultivationModel.TAG] as CultivationModel
        mField = intent.extras[FieldModel.TAG] as FieldModel

        textViewFieldName.text = mCultivation?.name.plus(getString(R.string.season)).plus(" ")
                .plus(mCultivation?.seasonName)

        floatingActionButtonAddProduction.setOnClickListener { addProduction() }

        superRecyclerViewCultivation.setLayoutManager(LinearLayoutManager(this))
        superRecyclerViewCultivation.adapter = mAdapter

        if (mPresenter == null)
            mPresenter = ProductionPresenter(mCultivation, mField?.totalArea)

        if (mMaterialDialogUtils == null)
            mMaterialDialogUtils = MaterialDialogUtils(this)

        mPresenter?.bindView(this, mAdapter)
        mPresenter?.loadProductions()
        updateMenuIcons()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.unBindView()
        mPresenter = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_production, menu)

        mMenuItemDelete = menu.findItem(R.id.menuProductionItemDelete)
        mMenuItemEdit = menu.findItem(R.id.menuProductionItemEdit)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            android.R.id.home -> onBackPressed()

            R.id.menuProductionItemEdit -> updateProduction()

            R.id.menuProductionItemDelete -> mPresenter?.deleteSelectedItems(mAdapter.selectedItens)
        }
        return true
    }

    override fun updateProducation(result: Double) {
        textViewTotalProduction.text = NumberFormat.getCurrencyInstance()
                .format(result).replace(NumberFormat.getCurrencyInstance()
                .currency.symbol, ""). plus(" ").plus(getString(R.string.bags_per_hectare))
    }

    override fun updateMenuIcons() {

        when (mAdapter.selectedItens.size) {

            0 -> {
                mMenuItemDelete?.isVisible = false
                mMenuItemEdit?.isVisible = false
            }

            1 -> {
                mMenuItemDelete?.isVisible = true
                mMenuItemEdit?.isVisible = true
            }

            !in(0..1) -> {
                mMenuItemDelete?.isVisible = true
                mMenuItemEdit?.isVisible = false
            }
        }
    }

    private fun addProduction() {
        mMaterialDialogUtils
                ?.getDialog(R.layout.new_production_view, R.string.new_production, mPresenter)?.show()
    }

    private fun updateProduction() {
        //We can select index 0 because the edit item will only be visible
        // when only 1 item is selected
        val field = mAdapter.selectedItens[0]
        val builder = mMaterialDialogUtils
                ?.getDialog(R.layout.new_production_view, R.string.production, mPresenter)

        val textViewProductionKey = builder?.build()?.findViewById(R.id.textViewProductionKey) as TextView
        textViewProductionKey.text = field.key

        val materialEditTextProductionArea: MoneyMaskMaterialEditText =
                builder.build().findViewById(R.id.materialEditTextProductionBags) as MoneyMaskMaterialEditText
        materialEditTextProductionArea.setTextFromDouble(field.bags)

        builder.show()
    }
}
