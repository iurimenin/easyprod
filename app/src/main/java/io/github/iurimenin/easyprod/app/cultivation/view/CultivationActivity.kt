package io.github.iurimenin.easyprod.app.cultivation.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSpinner
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.rengwuxian.materialedittext.MaterialEditText
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.cultivation.model.CultivationModel
import io.github.iurimenin.easyprod.app.cultivation.presenter.CultivationPresenter
import io.github.iurimenin.easyprod.app.field.model.FieldModel
import io.github.iurimenin.easyprod.app.season.model.SeasonModel
import io.github.iurimenin.easyprod.app.util.CallbackInterface
import kotlinx.android.synthetic.main.activity_cultivation.*

class CultivationActivity : AppCompatActivity(), CallbackInterface {

    private var mPresenter: CultivationPresenter? = null
    private var mMenuItemDelete: MenuItem? = null
    private var mMenuItemEdit: MenuItem? = null
    private var mField: FieldModel? = null

    private val mAdapter: CultivationAdapter = CultivationAdapter(this, ArrayList<CultivationModel>()) {
        mPresenter?.clickItem(it)
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

        mPresenter?.bindView(this, mAdapter)
        mPresenter?.loadCultivations()
        updateMenuIcons()
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

            R.id.menuCultivationItemDelete -> mPresenter?.deleteSelectedItems(mAdapter.selectedItens)
        }
        return true
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

    private fun addFiled() {
        val builder = MaterialDialog.Builder(this)
                .title(R.string.new_cultivation)
                .titleColorRes(R.color.colorPrimary)
                .contentColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
                .negativeColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .customView(R.layout.new_cultivation_view, true)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_cancel)
                .autoDismiss(false)
                .onAny { materialDialog, dialogAction ->
                    mPresenter?.saveCultivation(materialDialog, dialogAction == DialogAction.POSITIVE) }

        val appCompatSpinnerSeason =
                builder.build().findViewById(R.id.appCompatSpinnerSeason) as AppCompatSpinner

        appCompatSpinnerSeason.adapter = ArrayAdapter(this, R.layout.spinner_season, mPresenter?.mSeasons)

        builder.show()
    }

    private fun updateCultivation() {
        //We can select index 0 because the edit item will only be visible
        // when only 1 item is selected
        val cultivation = mAdapter.selectedItens[0]

        val builder = MaterialDialog.Builder(this)
                .title(R.string.cultivation)
                .titleColorRes(R.color.colorPrimary)
                .contentColor(ContextCompat.getColor(this, R.color.colorPrimaryText))
                .negativeColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                .positiveColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .customView(R.layout.new_cultivation_view, true)
                .positiveText(R.string.text_save)
                .negativeText(R.string.text_cancel)
                .autoDismiss(false)
                .onAny { materialDialog, dialogAction ->
                    mPresenter?.saveCultivation(materialDialog, dialogAction == DialogAction.POSITIVE) }

        val textViewCultivationKey = builder.build().findViewById(R.id.textViewCultivationKey) as TextView
        textViewCultivationKey.text = cultivation.key

        val materialEditTextCultivationName =
                builder.build().findViewById(R.id.materialEditTextCultivationName) as MaterialEditText
        materialEditTextCultivationName.setText(cultivation.name)

        val appCompatSpinnerSeason =
                builder.build().findViewById(R.id.appCompatSpinnerSeason) as AppCompatSpinner

        val seasonAdapter : ArrayAdapter<SeasonModel> = ArrayAdapter(this, R.layout.spinner_season, mPresenter?.mSeasons)
        appCompatSpinnerSeason.adapter = seasonAdapter

        appCompatSpinnerSeason.setSelection(seasonAdapter.getPosition(
                mPresenter?.mSeasons?.find { it.key == cultivation.seasonKey }))
        builder.show()
    }
}
