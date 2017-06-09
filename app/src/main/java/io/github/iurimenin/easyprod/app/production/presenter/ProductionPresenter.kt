package io.github.iurimenin.easyprod.app.production.presenter

import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.cultivation.model.CultivationModel
import io.github.iurimenin.easyprod.app.production.model.ProductionModel
import io.github.iurimenin.easyprod.app.production.view.ProductionActivity
import io.github.iurimenin.easyprod.app.production.view.ProductionAdapter
import io.github.iurimenin.easyprod.app.util.FirebaseUtils
import io.github.iurimenin.easyprod.app.util.MoneyMaskMaterialEditText
import io.github.iurimenin.easyprod.app.util.PresenterInterface
import io.github.iurimenin.easyprod.app.util.ProductionInterface
import io.github.iurimenin.easyprod.app.widget.model.WidgetModel
import io.github.iurimenin.easyprod.app.widget.utils.WidgetSharedPreferences
import io.github.iurimenin.easyprod.app.widget.view.LastProductionsWidget

/**
 * Created by Iuri Menin on 01/06/17.
 */
class ProductionPresenter(var mCultivation: CultivationModel?, val totalAreaField: Double ?)
    : PresenterInterface {

    private var mContext: Context? = null
    private var mAdapter: ProductionAdapter? = null
    private var mCallback: ProductionInterface? = null
    private var mWidgetPreferences : WidgetSharedPreferences? = null
    private val mProductionRef = FirebaseUtils().getProductionReference(mCultivation?.key!!)

    fun  bindView(productionActivity: ProductionActivity, adapter : ProductionAdapter) {
        this.mAdapter = adapter
        this.mContext = productionActivity
        this.mCallback = productionActivity
        this.mWidgetPreferences = WidgetSharedPreferences(productionActivity)
    }

    fun unBindView() {
        this.mAdapter = null
        this.mCallback = null
    }

    fun  clickItem(fieldModel: ProductionModel) {
//        val i = Intent(this.mContext, CultivationActivity::class.java)
//        i.putExtra(ProductionModel.TAG, fieldModel)
//        this.mContext?.startActivity(i)
    }

    fun loadProductions() {

        mProductionRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val vo = dataSnapshot.getValue(ProductionModel::class.java)
                mAdapter?.addItem(vo)
                updateTotalProduction()
                updateWidget()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                val updated = dataSnapshot.getValue(ProductionModel::class.java)
                mAdapter?.updateItem(updated)
                updateTotalProduction()
                updateWidget()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = dataSnapshot.getValue(ProductionModel::class.java)
                mAdapter?.removeItem(removed)
                mCallback?.updateMenuIcons(mAdapter?.itemCount)
                updateTotalProduction()
                updateWidget()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun save(materialDialog: MaterialDialog, isPositive : Boolean) {

        if (isPositive) {

            if (materialDialog.isValid()) {

                val production = ProductionModel(
                        (materialDialog.findViewById(R.id.textViewProductionKey) as TextView).text.toString(),
                        (materialDialog.findViewById(R.id.materialEditTextProductionBags) as MoneyMaskMaterialEditText).double)

                production.save(mCultivation?.key!!)
                materialDialog.dismiss()
                mAdapter?.removeSelecionts()
            }
        } else {
            materialDialog.dismiss()
        }
    }

    fun  deleteSelectedItems(selectedItens: ArrayList<ProductionModel>) {
        for (item in selectedItens)
            mProductionRef.child(item.key).removeValue()
    }

    private fun updateTotalProduction() {

        var totalBags : Double = 0.0
        mAdapter?.mProductions?.forEach { i -> totalBags += i.bags }

        val result : Double? = totalBags.div(totalAreaField!!)
        mCallback?.updateProduction(result ?: 0.0)
    }

    private fun updateWidget() {

        val list : ArrayList<WidgetModel> = ArrayList()
        mAdapter?.mProductions?.forEach {
            i -> list.add(
                WidgetModel(i.key, i.bags, mCultivation?.key, mCultivation?.name!!,
                        mCultivation?.seasonName!!))
        }

        mWidgetPreferences?.save(list)

        val dataUpdatedIntent = Intent(LastProductionsWidget.ACTION_DATA_UPDATED)
                .setPackage(mContext?.packageName)
        mContext?.sendBroadcast(dataUpdatedIntent)
    }
}

private fun  MaterialDialog.isValid(): Boolean {

    val materialEditTextProductionBags: MoneyMaskMaterialEditText =
            this.findViewById(R.id.materialEditTextProductionBags) as MoneyMaskMaterialEditText

    var valid = true

    if (materialEditTextProductionBags.text.toString().isNullOrEmpty() ||
            materialEditTextProductionBags.double == 0.0) {
        materialEditTextProductionBags.error =
                this.context?.getString(R.string.error_field_required_and_bigger_than_0)
        valid = false
    }

    return valid
}
