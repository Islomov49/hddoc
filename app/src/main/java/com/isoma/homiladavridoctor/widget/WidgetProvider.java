package com.isoma.homiladavridoctor.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;

import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_CREATE;

/**
 * Created by Пользователь on 16.03.2017.
 */

public class WidgetProvider extends AppWidgetProvider {
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    static public void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sPref = context.getSharedPreferences("informat", context.MODE_PRIVATE);;
        long creation_time = sPref.getLong(SAVED_CREATE, System.currentTimeMillis());
        int weeks = (int) ((System.currentTimeMillis() - creation_time) / 1000 / 60 / 60 / 24 / 7);
        if (weeks > 40) {
            weeks = 40;

        }
        if (weeks < 1) {
            weeks = 1;
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.tvWeekNumber, String.valueOf(weeks));

        String language = sPref.getString("language", "uz");
        if (language.matches(context.getResources().getString(R.string.ru)))
            views.setTextViewText(R.id.tvHafta, "Неделя");

        else
            views.setTextViewText(R.id.tvHafta, "Hafta");


        Intent home = new Intent(context, HomilaDavri.class);
        PendingIntent pendingIntentHome = PendingIntent.getActivity(context, 1, home, 0);
        views.setOnClickPendingIntent(R.id.ivHome, pendingIntentHome);

        Intent test = new Intent(context, HomilaDavri.class);
        test.putExtra(WidgetKeys.KEY_TO_INTENT, WidgetKeys.KEY_NEWSFEED);
        PendingIntent pendingIntentNewsfeed = PendingIntent.getActivity(context, 2, test, 0);
        views.setOnClickPendingIntent(R.id.ivTest, pendingIntentNewsfeed);

        Intent profile = new Intent(context, HomilaDavri.class);
        profile.putExtra(WidgetKeys.KEY_TO_INTENT, WidgetKeys.KEY_ARTICLE);
        PendingIntent pendingIntentArticle = PendingIntent.getActivity(context, 3, profile, 0);
        views.setOnClickPendingIntent(R.id.ivProfile, pendingIntentArticle);

        Intent info = new Intent(context, HomilaDavri.class);
        info.putExtra(WidgetKeys.KEY_TO_INTENT, WidgetKeys.KEY_INFO);
        PendingIntent pendingIntentInfo = PendingIntent.getActivity(context, 4, info, 0);
        views.setOnClickPendingIntent(R.id.ivInfo, pendingIntentInfo);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
