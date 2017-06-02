package io.github.iurimenin.easyprod.app.season.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.iurimenin.easyprod.app.season.model.SeasonModel

/**
 * Created by Iuri Menin on 02/06/17.
 */
class SeasonUtils(context: Context) {

    val gson = Gson()
    val type = object : TypeToken<ArrayList<SeasonModel>>() {}.type
    val sharedPref = context.getSharedPreferences("EASYPROD", Context.MODE_PRIVATE)

    fun addStoredSeasonsToList(listSeason: ArrayList<SeasonModel>): ArrayList<SeasonModel> {

        val jsonStored = sharedPref.getString(SeasonModel.TAG, "")

        if (!jsonStored.isNullOrEmpty()) {

            val listStored: ArrayList<SeasonModel> = gson.fromJson(jsonStored, type)

            listStored
                    .filterNot { it -> listSeason.contains(it) }
                    .forEach { it -> listSeason.add(it) }

            return listStored
        }

        return ArrayList()
    }

    fun  save(listSeason: ArrayList<SeasonModel>) {
        val editor = sharedPref.edit()
        editor.putString(SeasonModel.TAG, gson.toJson(listSeason))
        editor.apply()
    }
}