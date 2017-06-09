package io.github.iurimenin.easyprod.app.widget.view

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import android.widget.RemoteViews
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.app.production.view.ProductionActivity
import io.github.iurimenin.easyprod.app.view.LoginActivity
import io.github.iurimenin.easyprod.app.widget.service.ProductionsWidgetRemoteViewsService


/**
 * Implementation of App Widget functionality.
 */
class LastProductionsWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_DATA_UPDATED == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, javaClass))
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        }
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.last_productions_widget)

            // Create an Intent to launch MainActivity
            val intent = Intent(context, LoginActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.widget, pendingIntent)

            views.setRemoteAdapter(R.id.widget_list,
                    Intent(context, ProductionsWidgetRemoteViewsService::class.java))

            val clickIntentTemplate = Intent(context, ProductionActivity::class.java)

            val clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate)
            views.setEmptyView(R.id.widget_list, R.id.widget_empty)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        val  ACTION_DATA_UPDATED = "io.github.iurimenin.easyprod.app.ACTION_DATA_UPDATED"
    }
}

