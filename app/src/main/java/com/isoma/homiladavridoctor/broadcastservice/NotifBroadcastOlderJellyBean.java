package com.isoma.homiladavridoctor.broadcastservice;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;

/**
 * Created by Develop on 18.08.2015.
 */
public class NotifBroadcastOlderJellyBean extends BroadcastReceiver {

    int HAFTA_SONI ;
    long CREATE  ;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;
    Resources res;

    @Override
    public void onReceive(Context context, Intent intent) {
      try{  if(Build.VERSION.SDK_INT< Build.VERSION_CODES.JELLY_BEAN){
        sPref = context.getSharedPreferences("informat", context.MODE_PRIVATE);
        ed=sPref.edit();
        CREATE=sPref.getLong("yaralgan", 0);
        HAFTA_SONI=(int)((System.currentTimeMillis()-CREATE)/1000/60/60/24/7);
        res = context.getResources();
        if(HAFTA_SONI>40){
            HAFTA_SONI=40;
        }
        Intent firstIntent = new Intent(context,HomilaDavri.class);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, firstIntent, 0);

        NotificationCompat.Builder builder=(NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.minimain)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_icon))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND )
                .setContentTitle( Integer.toString(HAFTA_SONI) + context.getString(R.string.homilaning_haftasi))
                .setContentText(context.getString(R.string.homila_osib_bormoqda))
                .setContentIntent(pIntent);
        NotificationManager mNotifManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifManager.notify(1, builder.build());
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, CREATE + (long) (HAFTA_SONI + 1) * (long) 7 * 24 * 60 * 60 * 1000, pendingIntent);
        ed.putInt("getWeek", HAFTA_SONI);
      ed.apply();}

    }catch(Exception o){

      }}
}
