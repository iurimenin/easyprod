package io.github.iurimenin.easyprod.app.production.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.production.model.ProductionModel
import io.github.iurimenin.easyprod.app.util.CallbackInterface
import kotlinx.android.synthetic.main.item_production.view.*
import java.text.NumberFormat

/**
 * Created by Iuri Menin on 01/06/17.
 */
class ProductionAdapter(val mCallback : CallbackInterface,
                        val mProductions: ArrayList<ProductionModel>,
                        val clickListener: (ProductionModel) -> Unit) :
        RecyclerView.Adapter<ProductionAdapter.ViewHolder>() {

    val selectedItens = ArrayList<ProductionModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductionAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_production, parent, false)
        return ProductionAdapter.ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ProductionAdapter.ViewHolder, position: Int) {
        holder.bindProduction(mProductions[position], selectedItens, holder.itemView.context, mCallback)
    }

    override fun getItemCount() = mProductions.size

    class ViewHolder(view: View,
                     val clickListener: (ProductionModel) -> Unit) :
            RecyclerView.ViewHolder(view) {

        fun bindProduction(field: ProductionModel, selectedItens: ArrayList<ProductionModel>, context: Context,
                      callback: CallbackInterface) {
            with(field) {
                itemView.textViewProduction.text = NumberFormat.getCurrencyInstance()
                        .format(bags).replace(NumberFormat.getCurrencyInstance()
                        .currency.symbol, "")
                itemView.constraintLayoutItemProduction.setOnClickListener {
                    if (selectedItens.size > 0)
                        selectDeselectItem(itemView, field, selectedItens, context, callback)
                    else
                        clickListener(this)
                }
                itemView.constraintLayoutItemProduction.setOnLongClickListener {
                    selectDeselectItem(itemView, field, selectedItens, context, callback); true
                }
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorIcons))
            }
        }

        private fun selectDeselectItem(itemView: View?, field: ProductionModel,
                                       selectedItens: ArrayList<ProductionModel>, context: Context,
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

    fun addItem(field : ProductionModel) {
        mProductions.add(field)
        this.notifyDataSetChanged()
    }

    fun  updateItem(updated: ProductionModel) {

        mProductions
                .filter { it.key == updated.key }
                .forEach {
                    it.bags = updated.bags
                }
        this.notifyDataSetChanged()
    }

    fun removeItem(field: ProductionModel) {
        mProductions.remove(field)
        selectedItens.remove(field)
        this.notifyDataSetChanged()
    }

    fun removeSelecionts() {
        selectedItens.clear()
        mCallback.updateMenuIcons()
        this.notifyDataSetChanged()
    }
}
