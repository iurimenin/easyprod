package io.github.iurimenin.easyprod.app.cultivation.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView
import com.rengwuxian.materialedittext.MaterialEditText
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.cultivation.model.CultivationModel
import io.github.iurimenin.easyprod.app.cultivation.presenter.CultivationPresenter
import io.github.iurimenin.easyprod.app.field.model.FieldModel
import io.github.iurimenin.easyprod.app.season.model.SeasonModel
import io.github.iurimenin.easyprod.app.util.MaterialDialogUtils
import io.github.iurimenin.easyprod.app.view.EasyProdActitivty
import kotlinx.android.synthetic.main.activity_cultivation.*

class CultivationActivity : EasyProdActitivty() {

    private var mField: FieldModel? = null
    private var mPresenter: CultivationPresenter? = null
    private var mMaterialDialogUtils : MaterialDialogUtils? = null

    private val mAdapter: CultivationAdapter = CultivationAdapter(this, ArrayList<CultivationModel>()) {
        mPresenter?.clickItem(it, mField)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cultivation)

        val arrowBack = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp)
        arrowBack.setColorFilter(ContextCompat.getColor(this, android.R.color.white),
                PorterDuff.Mode.SRC_ATOP)

        supportActionBar?.apply {
            title = getString(R.string.cultivations)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(arrowBack)
        }

        mField = intent.extras[FieldModel.TAG] as FieldModel

        textViewFieldName.text = mField?.name

        floatingActionButtonAddCultivation.setOnClickListener { addFiled() }

        superRecyclerViewCultivations.setLayoutManager(LinearLayoutManager(this))
        superRecyclerViewCultivations.adapter = mAdapter

        if (mPresenter == null)
            mPresenter = CultivationPresenter(mField?.key)

        if (mMaterialDialogUtils == null)
            mMaterialDialogUtils = MaterialDialogUtils(this)

        mPresenter?.bindView(this, mAdapter)
        mPresenter?.loadCultivations()
        super.updateMenuIcons(mAdapter.itemCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.unBindView()
        mPresenter = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_cultivation, menu)

        mMenuItemDelete = menu.findItem(R.id.menuCultivationItemDelete)
        mMenuItemEdit = menu.findItem(R.id.menuCultivationItemEdit)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {

            android.R.id.home -> onBackPressed()

            R.id.menuCultivationItemEdit -> updateCultivation()

            R.id.menuCultivationItemDelete -> mPresenter?.deleteSelectedItems(mAdapter.selectedItems)
        }
        return true
    }

    private fun addFiled() {
        val builder = mMaterialDialogUtils
                ?.getDialog(R.layout.new_cultivation_view, R.string.new_cultivation, mPresenter)

        val appCompatSpinnerSeason =
                builder?.build()?.findViewById(R.id.appCompatSpinnerSeason) as AppCompatSpinner

        appCompatSpinnerSeason.adapter =
                ArrayAdapter(this, R.layout.spinner_season, mPresenter?.mSeasons)

        builder.show()
    }

    private fun updateCultivation() {
        //We can select index 0 because the edit item will only be visible
        // when only 1 item is selected
        val cultivation = mAdapter.selectedItems[0]

        val builder = mMaterialDialogUtils
                ?.getDialog(R.layout.new_cultivation_view, R.string.cultivation, mPresenter)

        val textViewCultivationKey =
                builder?.build()?.findViewById(R.id.textViewCultivationKey) as TextView
        textViewCultivationKey.text = cultivation.key

        val materialEditTextCultivationName =
                builder.build().findViewById(R.id.materialEditTextCultivationName) as MaterialEditText
        materialEditTextCultivationName.setText(cultivation.name)

        val appCompatSpinnerSeason =
                builder.build().findViewById(R.id.appCompatSpinnerSeason) as AppCompatSpinner

        val seasonAdapter : ArrayAdapter<SeasonModel> = ArrayAdapter(this, R.layout.spinner_season,
                mPresenter?.mSeasons)
        appCompatSpinnerSeason.adapter = seasonAdapter

        appCompatSpinnerSeason.setSelection(seasonAdapter.getPosition(
                mPresenter?.mSeasons?.find { it.key == cultivation.seasonKey }))
        builder.show()
    }
}
