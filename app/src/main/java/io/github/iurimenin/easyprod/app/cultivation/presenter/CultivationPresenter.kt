package io.github.iurimenin.easyprod.app.cultivation.presenter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.AppCompatSpinner
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rengwuxian.materialedittext.MaterialEditText
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.cultivation.model.CultivationModel
import io.github.iurimenin.easyprod.app.cultivation.view.CultivationActivity
import io.github.iurimenin.easyprod.app.cultivation.view.CultivationAdapter
import io.github.iurimenin.easyprod.app.field.model.FieldModel
import io.github.iurimenin.easyprod.app.production.view.ProductionActivity
import io.github.iurimenin.easyprod.app.season.model.SeasonModel
import io.github.iurimenin.easyprod.app.util.CallbackInterface
import io.github.iurimenin.easyprod.app.util.FirebaseUtils
import io.github.iurimenin.easyprod.app.util.PresenterInterface

/**
 * Created by Iuri Menin on 01/06/17.
 */
class CultivationPresenter(var mFieldKey : String?) : PresenterInterface {

    private val mGson = Gson()
    val mSeasons  = ArrayList<SeasonModel>()
    private var mContext: Context? = null
    private var mAdapter: CultivationAdapter? = null
    private var mCallback: CallbackInterface? = null
    private val mCultivationRef = FirebaseUtils().getFieldReference(mFieldKey!!)

    fun  bindView(CultivationActivity: CultivationActivity, adapter : CultivationAdapter) {
        this.mAdapter = adapter
        this.mContext = CultivationActivity
        this.mCallback = CultivationActivity
        this.loadSeasons()
    }

    fun unBindView() {
        this.mAdapter = null
        this.mCallback = null
    }

    fun  clickItem(cultivationModel: CultivationModel, mField: FieldModel?) {
        val i = Intent(this.mContext, ProductionActivity::class.java)
        i.putExtra(CultivationModel.TAG, cultivationModel)
        i.putExtra(FieldModel.TAG, mField)
        this.mContext?.startActivity(i)
    }

    fun loadCultivations() {

        mCultivationRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val vo = dataSnapshot.getValue(CultivationModel::class.java)
                mAdapter?.addItem(vo)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                val updated = dataSnapshot.getValue(CultivationModel::class.java)
                mAdapter?.updateItem(updated)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = dataSnapshot.getValue(CultivationModel::class.java)
                mAdapter?.removeItem(removed)
                mCallback?.updateMenuIcons(mAdapter?.itemCount)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun save(materialDialog: MaterialDialog, isPositive : Boolean) {

        if (isPositive) {

            val textViewCultivationKey: TextView =
                    materialDialog.findViewById(R.id.textViewCultivationKey) as TextView

            val materialEditTextCultivationName: MaterialEditText =
                    materialDialog.findViewById(R.id.materialEditTextCultivationName) as MaterialEditText

            val appCompatSpinnerSeason: AppCompatSpinner =
                    materialDialog.findViewById(R.id.appCompatSpinnerSeason) as AppCompatSpinner

            val season : SeasonModel = appCompatSpinnerSeason.selectedItem as SeasonModel
            val fieldName = materialEditTextCultivationName.text.toString()
            if (fieldName.isNullOrEmpty())
                materialEditTextCultivationName.error =
                        this.mContext?.getString(R.string.error_field_required)
            else {

                val field = CultivationModel(textViewCultivationKey.text.toString(),
                        fieldName, season.key, season.toString())
                field.save(mFieldKey!!)
                materialDialog.dismiss()
                mAdapter?.removeSelection()
            }
        } else {
            materialDialog.dismiss()
        }
    }

    fun  deleteSelectedItems(selectedItens: ArrayList<CultivationModel>) {
        for (item in selectedItens)
            mCultivationRef.child(item.key).removeValue()
    }

    private fun loadSeasons() {

        val type = object : TypeToken<ArrayList<SeasonModel>>() {}.type
        val sharedPref = mContext?.getSharedPreferences("EASYPROD", Context.MODE_PRIVATE)

        val jsonStored = sharedPref?.getString(SeasonModel.TAG, "")

        if (!jsonStored.isNullOrEmpty()) {

            val listStored : ArrayList<SeasonModel> = mGson.fromJson(jsonStored, type)

            listStored
                    .filterNot { it -> mSeasons.contains(it) }
                    .forEach { it -> mSeasons.add(it) }
        }
    }
}