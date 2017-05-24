package io.github.iurimenin.easyprod.farm.util

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.iurimenin.easyprod.CallbackInterface
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.farm.model.FarmModel
import kotlinx.android.synthetic.main.item_farm.view.*

/**
 * Created by Iuri Menin on 23/05/17.
 */
class FarmAdapter(val mCallback : CallbackInterface,
                  val mFarms: ArrayList<FarmModel>,
                  val clickListener: (FarmModel) -> Unit) :
        RecyclerView.Adapter<FarmAdapter.ViewHolder>() {

    val selectedItens = ArrayList<FarmModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_farm, parent, false)
        return FarmAdapter.ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: FarmAdapter.ViewHolder, position: Int) {
        holder.bindFarm(mFarms[position], selectedItens, holder.itemView.context, mCallback)
    }

    override fun getItemCount() = mFarms.size

    class ViewHolder(view: View,
                     val clickListener: (FarmModel) -> Unit) :
            RecyclerView.ViewHolder(view) {

        fun bindFarm(farm: FarmModel, selectedItens: ArrayList<FarmModel>, context: Context,
                     callback: CallbackInterface) {
            with(farm) {
                itemView.textViewFarm.text = name
                itemView.constraintLayoutItemFarm.setOnClickListener {
                    if (selectedItens.size > 0)
                        selectDeselectItem(itemView, farm, selectedItens, context, callback)
                    else
                        clickListener(this)
                }
                itemView.constraintLayoutItemFarm.setOnLongClickListener {
                    selectDeselectItem(itemView, farm, selectedItens, context, callback); true
                }
            }
        }

        private fun selectDeselectItem(itemView: View?, farm: FarmModel,
                               selectedItens: ArrayList<FarmModel>, context: Context,
                                       callback : CallbackInterface) {
            if(selectedItens.contains(farm)){
                selectedItens.remove(farm);
                itemView?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorIcons));
            } else {
                selectedItens.add(farm);
                itemView?.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDivider));
            }

            callback.executeCallback()
        }
    }

    fun addItem(farm : FarmModel) {
        mFarms.add(farm)
        this.notifyDataSetChanged()
    }

    fun  updateItem(updated: FarmModel) {

        mFarms
                .filter { it.key == updated.key }
                .forEach { it.name = updated.name }
        this.notifyDataSetChanged()
    }

    fun removeItem(farm: FarmModel) {
        mFarms.remove(farm)
        selectedItens.remove(farm)
        this.notifyDataSetChanged()
    }
}