package io.github.iurimenin.easyprod.app.presenter

import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.model.FieldModel
import io.github.iurimenin.easyprod.app.util.FirebaseUtils
import io.github.iurimenin.easyprod.app.view.FieldActivity

/**
 * Created by Iuri Menin on 25/05/17.
 */
class FieldPresenter(var mFarmKey : String?) {

    private var mFieldActivity: FieldActivity? = null
    private val mFieldRef = FirebaseUtils().getFieldReference(mFarmKey!!)

    fun  bindView(fieldActivity: FieldActivity) {
        this.mFieldActivity = fieldActivity
    }

    fun unBindView() {
        this.mFieldActivity = null
    }

    fun  clickItem(it: FieldModel) {
        Toast.makeText(mFieldActivity, "Click", Toast.LENGTH_SHORT).show()
    }

    fun loadFields() {

        mFieldRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val vo = dataSnapshot.getValue(FieldModel::class.java)
                mFieldActivity?.addItem(vo)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                val updated = dataSnapshot.getValue(FieldModel::class.java)
                mFieldActivity?.updateItem(updated)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = dataSnapshot.getValue(FieldModel::class.java)
                mFieldActivity?.removeItem(removed)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun saveField(materialDialog: MaterialDialog, isPositive : Boolean) {

        if (isPositive) {

            val editTextFieldName: EditText =
                    materialDialog.findViewById(R.id.materialEditTextFieldName) as EditText

            val textViewFieldKey: TextView =
                    materialDialog.findViewById(R.id.textViewFieldKey) as TextView

            val farmName = editTextFieldName.text.toString()
            if (farmName.isNullOrEmpty())
                editTextFieldName.error = this.mFieldActivity?.getString(R.string.error_field_required)
            else {

                val field = FieldModel(textViewFieldKey.text.toString(), farmName)
                field.save(mFarmKey!!)
                materialDialog.dismiss()
                mFieldActivity?.removeSelecionts()
            }
        } else {
            materialDialog.dismiss()
        }
    }

    fun  deleteSelectedItems(selectedItens: ArrayList<FieldModel>) {
        for (item in selectedItens)
            mFieldRef.child(item.key).removeValue()
    }
}