package io.github.iurimenin.easyprod.app.widget.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.iurimenin.easyprod.app.widget.model.WidgetModel

/**
 * Created by Iuri Menin on 09/06/17.
 */
class WidgetSharedPreferences(var context: Context) {

    val gson = Gson()
    val type = object : TypeToken<ArrayList<WidgetModel>>() {}.type
    val sharedPref = context.getSharedPreferences("EASYPROD", Context.MODE_PRIVATE)

    fun getStored(): ArrayList<WidgetModel> {

        val jsonStored = sharedPref.getString(WidgetModel.TAG, "")

        if (!jsonStored.isNullOrEmpty()) {

            return gson.fromJson(jsonStored, type)
        }

        return ArrayList()
    }

    fun  save(listSeason: ArrayList<WidgetModel>) {
        val editor = sharedPref.edit()
        editor.putString(WidgetModel.TAG, gson.toJson(listSeason))
        editor.apply()
    }
}