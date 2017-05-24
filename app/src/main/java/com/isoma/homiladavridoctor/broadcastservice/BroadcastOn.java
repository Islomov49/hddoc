package com.isoma.homiladavridoctor.broadcastservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;

import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_CREATE;
import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_WEEK;

/**
 * Created by Develop on 18.08.2015.
 */
public class BroadcastOn extends BroadcastReceiver {
    private AlarmManager am;
    private SharedPreferences sPref;
    private SharedPreferences.Editor ed;
    int HAFTA_SONI_TEK;
    private long create;
    private long creationTime;
    private int WEEKS_NUMBER;
    Resources res;

    @Override
    public void onReceive(Context context, Intent intent) {
        sPref = context.getSharedPreferences("informat", Context.MODE_PRIVATE);
        ed = sPref.edit();
        HAFTA_SONI_TEK = sPref.getInt(SAVED_WEEK, 1);
        create = sPref.getLong(SAVED_CREATE, 0);
        WEEKS_NUMBER = (int) ((System.currentTimeMillis() - create) / 1000 / 60 / 60 / 24 / 7);
        res = context.getResources();
        if (WEEKS_NUMBER > 40) {
            WEEKS_NUMBER = 40;
        }
        if (WEEKS_NUMBER < 1) {
            WEEKS_NUMBER = 1;
        }
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intente;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            intente = new Intent(context, NotifBroatcastJellyBean.class);

        } else {
            intente = new Intent(context, NotifBroadcastOlderJellyBean.class);
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intente, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
        creationTime = sPref.getLong(SAVED_CREATE, System.currentTimeMillis());
        long DingDing = creationTime + (long) (WEEKS_NUMBER + 1) * (long) 7 * (long) 24 * (long) 60 * (long) 60 * (long) 1000;
        am.set(AlarmManager.RTC_WAKEUP, DingDing, pendingIntent);
    }
}
