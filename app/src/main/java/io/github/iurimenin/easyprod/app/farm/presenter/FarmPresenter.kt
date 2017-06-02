package io.github.iurimenin.easyprod.app.farm.presenter

import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.farm.model.FarmModel
import io.github.iurimenin.easyprod.app.farm.view.FarmActivity
import io.github.iurimenin.easyprod.app.farm.view.FarmAdapter
import io.github.iurimenin.easyprod.app.field.view.FieldActivity
import io.github.iurimenin.easyprod.app.season.model.SeasonModel
import io.github.iurimenin.easyprod.app.util.CallbackInterface
import io.github.iurimenin.easyprod.app.util.FirebaseUtils
import io.github.iurimenin.easyprod.app.view.AboutActivity


/**
 * Created by Iuri Menin on 23/05/17.
 */
class FarmPresenter {

    private var mAdapter: FarmAdapter? = null
    private var mContext: Context? = null
    private var mCallback: CallbackInterface? = null
    private val farmRef = FirebaseUtils().getFarmReference()

    fun  bindView(farmActivity: FarmActivity, adapter : FarmAdapter) {
        this.mCallback = farmActivity
        this.mAdapter = adapter
        this.mContext = farmActivity

        this.syncSeasons(farmActivity)
    }

    private fun  syncSeasons(context : Context) {

        // Sync all seasons to use
        val gson = Gson()
        val listSeason = ArrayList<SeasonModel>()
        val seasonReference = FirebaseUtils().getSeasonReference()
        val type = object : TypeToken<ArrayList<SeasonModel>>() {}.type
        val sharedPref = context.getSharedPreferences("EASYPROD", Context.MODE_PRIVATE)

        seasonReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val vo = dataSnapshot.getValue(SeasonModel::class.java)

                val jsonStored = sharedPref.getString(SeasonModel.TAG, "")

                if (!jsonStored.isNullOrEmpty()) {

                    val listStored : ArrayList<SeasonModel> = gson.fromJson(jsonStored, type)

                    listStored
                            .filterNot { it -> listSeason.contains(it) }
                            .forEach { it -> listSeason.add(it) }
                }

                if (!listSeason.contains(vo))
                    listSeason.add(vo)

                val editor = sharedPref.edit()
                editor.putString(SeasonModel.TAG, gson.toJson(listSeason))
                editor.apply()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val updated = dataSnapshot.getValue(SeasonModel::class.java)

                val jsonStored = sharedPref.getString(SeasonModel.TAG, "")

                if (!jsonStored.isNullOrEmpty()) {

                    val listStored : ArrayList<SeasonModel> = gson.fromJson(jsonStored, type)

                    listStored
                            .filterNot { it -> listSeason.contains(it) }
                            .forEach { it -> listSeason.add(it) }
                }

                listSeason
                        .filter { it.key == updated.key }
                        .forEach {
                            it.startYear = updated.startYear
                            it.endYear = updated.endYear
                        }

                val editor = sharedPref.edit()
                editor.putString(SeasonModel.TAG, gson.toJson(listSeason))
                editor.apply()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val vo = dataSnapshot.getValue(SeasonModel::class.java)

                val jsonStored = sharedPref.getString(SeasonModel.TAG, "")

                if (!jsonStored.isNullOrEmpty()) {

                    val listStored : ArrayList<SeasonModel> = gson.fromJson(jsonStored, type)

                    listStored
                            .filterNot { it -> listSeason.contains(it) }
                            .forEach { it -> listSeason.add(it) }
                }

                listSeason.remove(vo)

                val editor = sharedPref.edit()
                editor.putString(SeasonModel.TAG, gson.toJson(listSeason))
                editor.apply()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun unBindView() {
        this.mAdapter = null
        this.mCallback = null
    }

    fun loadFarms() {

        farmRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val vo = dataSnapshot.getValue(FarmModel::class.java)
                mAdapter?.addItem(vo)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                val updated = dataSnapshot.getValue(FarmModel::class.java)
                mAdapter?.updateItem(updated)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = dataSnapshot.getValue(FarmModel::class.java)
                mAdapter?.removeItem(removed)
                mCallback?.updateMenuIcons()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun deleteSelectedItems(selectedItens: ArrayList<FarmModel>) {
        for (item in selectedItens)
            farmRef.child(item.key).removeValue()
    }

    fun clickItem (farmModel: FarmModel) {
        val i = Intent(this.mContext, FieldActivity::class.java)
        i.putExtra(FarmModel.TAG, farmModel)
        this.mContext?.startActivity(i)
    }

    fun showAbout () {
        val i = Intent(this.mContext, AboutActivity::class.java)
        this.mContext?.startActivity(i)
    }

    fun saveFarm(materialDialog: MaterialDialog, isPositive : Boolean) {

        if (isPositive) {

            val editTextFarmName: EditText =
                    materialDialog.findViewById(R.id.materialEditTextFarmName) as EditText

            val textViewFarmKey: TextView =
                    materialDialog.findViewById(R.id.textViewFarmKey) as TextView

            val farmName = editTextFarmName.text.toString()
            if (farmName.isNullOrEmpty())
                editTextFarmName.error = this.mContext?.getString(R.string.error_field_required)
            else {

                val farm = FarmModel(textViewFarmKey.text.toString(), farmName)
                farm.save()
                materialDialog.dismiss()
                mAdapter?.removeSelecionts()
            }
        } else {
            materialDialog.dismiss()
        }
    }
}