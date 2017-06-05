package io.github.iurimenin.easyprod.app.field.presenter

import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.rengwuxian.materialedittext.MaterialEditText
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.cultivation.view.CultivationActivity
import io.github.iurimenin.easyprod.app.field.model.FieldModel
import io.github.iurimenin.easyprod.app.field.view.FieldActivity
import io.github.iurimenin.easyprod.app.field.view.FieldAdapter
import io.github.iurimenin.easyprod.app.util.CallbackInterface
import io.github.iurimenin.easyprod.app.util.FirebaseUtils
import io.github.iurimenin.easyprod.app.util.MoneyMaskMaterialEditText
import io.github.iurimenin.easyprod.app.util.PresenterInterface

/**
 * Created by Iuri Menin on 25/05/17.
 */
class FieldPresenter(var mFarmKey : String?) : PresenterInterface {

    private var mAdapter: FieldAdapter? = null
    private var mCallback: CallbackInterface? = null
    private var mContext: Context? = null
    private val mFieldRef = FirebaseUtils().getFieldReference(mFarmKey!!)

    fun  bindView(fieldActivity: FieldActivity, adapter : FieldAdapter) {
        this.mAdapter = adapter
        this.mContext = fieldActivity
        this.mCallback = fieldActivity
    }

    fun unBindView() {
        this.mAdapter = null
        this.mCallback = null
    }

    fun  clickItem(fieldModel: FieldModel) {
        val i = Intent(this.mContext, CultivationActivity::class.java)
        i.putExtra(FieldModel.TAG, fieldModel)
        this.mContext?.startActivity(i)
    }

    fun loadFields() {

        mFieldRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val vo = dataSnapshot.getValue(FieldModel::class.java)
                mAdapter?.addItem(vo)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                val updated = dataSnapshot.getValue(FieldModel::class.java)
                mAdapter?.updateItem(updated)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = dataSnapshot.getValue(FieldModel::class.java)
                mAdapter?.removeItem(removed)
                mCallback?.updateMenuIcons()
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

                val field = FieldModel(
                        (materialDialog.findViewById(R.id.textViewFieldKey) as TextView).text.toString(),
                        (materialDialog.findViewById(R.id.materialEditTextFieldName) as TextView).text.toString(),
                        (materialDialog.findViewById(R.id.materialEditTextFieldArea) as MoneyMaskMaterialEditText).double)

                field.save(mFarmKey!!)
                materialDialog.dismiss()
                mAdapter?.removeSelecionts()
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

private fun  MaterialDialog.isValid(): Boolean {

    val materialEditTextFieldName: MaterialEditText =
            this.findViewById(R.id.materialEditTextFieldName) as MaterialEditText

    val materialEditTextFieldArea: MoneyMaskMaterialEditText =
            this.findViewById(R.id.materialEditTextFieldArea) as MoneyMaskMaterialEditText

    var valid = true

    if (materialEditTextFieldName.text.toString().isNullOrEmpty()) {
        materialEditTextFieldName.error =
                this.context?.getString(R.string.error_field_required)
        valid = false
    }

    if (materialEditTextFieldArea.text.toString().isNullOrEmpty() ||
            materialEditTextFieldArea.double == 0.0) {
        materialEditTextFieldArea.error =
                this.context?.getString(R.string.error_field_required_and_bigger_than_0)
        valid = false
    }

    return valid
}
