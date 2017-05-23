package io.github.iurimenin.easyprod.farm.util

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.farm.model.FarmModel
import kotlinx.android.synthetic.main.item_farm.view.*

/**
 * Created by Iuri Menin on 23/05/17.
 */
class FarmAdapter(val mFarms: ArrayList<FarmModel>, val itemClick: (FarmModel) -> Unit) :
        RecyclerView.Adapter<FarmAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_farm, parent, false)
        return FarmAdapter.ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: FarmAdapter.ViewHolder, position: Int) {
        holder.bindForecast(mFarms[position])
    }

    override fun getItemCount() = mFarms.size

    class ViewHolder(view: View, val itemClick: (FarmModel) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bindForecast(farm: FarmModel) {
            with(farm) {
                itemView.textViewFarm.text = farm.name
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }

    fun addItem(farm : FarmModel) {
        mFarms.add(farm)
        this.notifyDataSetChanged()
    }

    fun  updateItem(updated: FarmModel) {

        for (farm in mFarms){
            if (farm.key.equals(updated.key))
                farm.name = updated.name
        }
        this.notifyDataSetChanged();
    }

    fun removeItem(farm: FarmModel) {
        mFarms.remove(farm)
        this.notifyDataSetChanged()
    }
}