package io.github.iurimenin.easyprod.app.widget.service

import android.content.Intent
import android.os.Binder
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.widget.model.WidgetModel
import io.github.iurimenin.easyprod.app.widget.utils.WidgetSharedPreferences
import java.text.NumberFormat


/**
 * Created by Iuri Menin on 09/06/17.
 */
class ProductionsWidgetRemoteViewsService : RemoteViewsService() {

    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {

        return object : RemoteViewsService.RemoteViewsFactory {

            private var listItens: ArrayList<WidgetModel> = ArrayList()
            private val widgetPreferences : WidgetSharedPreferences =
                    WidgetSharedPreferences(applicationContext)


            override fun onCreate() {
                // Nothing to do
            }

            override fun onDataSetChanged() {

                if (!listItens.isEmpty()) {
                    listItens.clear()
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                val identityToken = Binder.clearCallingIdentity()
                listItens = widgetPreferences.getStored()

                Binder.restoreCallingIdentity(identityToken)
            }

            override fun onDestroy() {
                if (!listItens.isEmpty()) {
                    listItens.clear()
                }
            }

            override fun getCount(): Int {
                return listItens.size
            }

            override fun getViewAt(position: Int): RemoteViews? {
                if (position == AdapterView.INVALID_POSITION ||
                        listItens.isEmpty()) {
                    return null
                }

                val widgetItem = listItens[position]

                val views = RemoteViews(packageName, R.layout.widget_list_item)

                views.setTextViewText(R.id.textViewWidgetCultivation, widgetItem.cultivationName)
                views.setTextViewText(R.id.textViewWidgetSeason, widgetItem.seasonName)
                views.setTextViewText(R.id.textViewWidgetBags, NumberFormat.getCurrencyInstance()
                        .format(widgetItem.bags).replace(NumberFormat.getCurrencyInstance()
                        .currency.symbol, ""). plus(" ").plus(getString(R.string.bags_per_hectare)))

                return views
            }

            override fun getLoadingView(): RemoteViews {
                return RemoteViews(packageName, R.layout.widget_list_item)
            }

            override fun getViewTypeCount(): Int {
                return 1
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun hasStableIds(): Boolean {
                return true
            }
        }
    }

}