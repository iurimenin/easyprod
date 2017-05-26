package io.github.iurimenin.easyprod.app.presenter

import android.content.Intent
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.model.FarmModel
import io.github.iurimenin.easyprod.app.util.FirebaseUtils
import io.github.iurimenin.easyprod.app.view.FarmActivity
import io.github.iurimenin.easyprod.app.view.FieldActivity
import io.github.iurimenin.easyprod.app.view.LoginActivity

/**
 * Created by Iuri Menin on 23/05/17.
 */
class FarmPresenter {

    private var mFarmActivity: FarmActivity? = null
    private val farmRef = FirebaseUtils().getFarmReference()

    fun  bindView(farmActivity: FarmActivity) {
        this.mFarmActivity = farmActivity
    }

    fun unBindView() {
        this.mFarmActivity = null
    }

    fun loadFarms() {

        farmRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val vo = dataSnapshot.getValue(FarmModel::class.java)
                mFarmActivity?.addItem(vo)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                val updated = dataSnapshot.getValue(FarmModel::class.java)
                mFarmActivity?.updateItem(updated)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = dataSnapshot.getValue(FarmModel::class.java)
                mFarmActivity?.removeItem(removed)
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

     fun logout() {
         if (this.mFarmActivity != null){
             AuthUI.getInstance()
                     .signOut(this.mFarmActivity!!)
                     .addOnCompleteListener {
                         // user is now signed out
                         this.mFarmActivity?.startActivity(Intent(this.mFarmActivity, LoginActivity::class.java))
                         this.mFarmActivity?.finish()
                     }
         }
    }

    fun clickItem (farmModel: FarmModel) {
        val i = Intent(this.mFarmActivity, FieldActivity::class.java)
        i.putExtra(FarmModel.TAG, farmModel)
        this.mFarmActivity?.startActivity(i)
    }

    fun saveFarm(materialDialog: MaterialDialog, isPositive : Boolean) {

        if (isPositive) {

            val editTextFarmName: EditText =
                    materialDialog.findViewById(R.id.materialEditTextFarmName) as EditText

            val textViewFarmKey: TextView =
                    materialDialog.findViewById(R.id.textViewFarmKey) as TextView

            val farmName = editTextFarmName.text.toString()
            if (farmName.isNullOrEmpty())
                editTextFarmName.error = this.mFarmActivity?.getString(R.string.error_field_required)
            else {

                val farm = FarmModel(textViewFarmKey.text.toString(), farmName)
                farm.save()
                materialDialog.dismiss()
                mFarmActivity?.removeSelecionts()
            }
        } else {
            materialDialog.dismiss()
        }
    }
}