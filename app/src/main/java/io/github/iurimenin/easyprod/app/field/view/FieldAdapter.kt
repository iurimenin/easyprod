package io.github.iurimenin.easyprod.app.field.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.field.model.FieldModel
import io.github.iurimenin.easyprod.app.util.CallbackInterface
import kotlinx.android.synthetic.main.item_field.view.*

/**
 * Created by Iuri Menin on 23/05/17.
 */
class FieldAdapter(val mCallback : CallbackInterface,
                   val mFields: ArrayList<FieldModel>,
                   val clickListener: (FieldModel) -> Unit) :
        RecyclerView.Adapter<FieldAdapter.ViewHolder>() {

    val selectedItens = ArrayList<FieldModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_field, parent, false)
        return FieldAdapter.ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: FieldAdapter.ViewHolder, position: Int) {
        holder.bindFarm(mFields[position], selectedItens, holder.itemView.context, mCallback)
    }

    override fun getItemCount() = mFields.size

    class ViewHolder(view: View,
                     val clickListener: (FieldModel) -> Unit) :
            RecyclerView.ViewHolder(view) {

        fun bindFarm(field: FieldModel, selectedItens: ArrayList<FieldModel>, context: Context,
                     callback: CallbackInterface) {
            with(field) {
                itemView.textViewField.text = name
                itemView.constraintLayoutItemField.setOnClickListener {
                    if (selectedItens.size > 0)
                        selectDeselectItem(itemView, field, selectedItens, context, callback)
                    else
                        clickListener(this)
                }
                itemView.constraintLayoutItemField.setOnLongClickListener {
                    selectDeselectItem(itemView, field, selectedItens, context, callback); true
                }
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorIcons))
            }
        }

        private fun selectDeselectItem(itemView: View?, field: FieldModel,
                                       selectedItens: ArrayList<FieldModel>, context: Context,
                                       callback : CallbackInterface) {
            if(selectedItens.contains(field)){
                selectedItens.remove(field)
                itemView?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorIcons))
            } else {
                selectedItens.add(field)
                itemView?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDivider))
            }

            callback.updateMenuIcons()
        }
    }

    fun addItem(field : FieldModel) {
        mFields.add(field)
        this.notifyDataSetChanged()
    }

    fun  updateItem(updated: FieldModel) {

        mFields
                .filter { it.key == updated.key }
                .forEach {
                    it.name = updated.name
                    it.totalArea = updated.totalArea
                }
        this.notifyDataSetChanged()
    }

    fun removeItem(field: FieldModel) {
        mFields.remove(field)
        selectedItens.remove(field)
        this.notifyDataSetChanged()
    }

    fun removeSelecionts() {
        selectedItens.clear()
        mCallback.updateMenuIcons()
        this.notifyDataSetChanged()
    }
}