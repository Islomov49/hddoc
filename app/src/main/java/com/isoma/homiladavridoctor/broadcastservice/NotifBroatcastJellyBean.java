package com.isoma.homiladavridoctor.broadcastservice;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.isoma.homiladavridoctor.HomilaDavri;
import com.isoma.homiladavridoctor.R;

import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_CREATE;
import static com.isoma.homiladavridoctor.systemic.HomilaConstants.SAVED_WEEK;
import static com.isoma.homiladavridoctor.utils.GeneralConstants.Imagees;

/**
 * Created by Develop on 18.08.2015.
 */
public class NotifBroatcastJellyBean extends BroadcastReceiver {


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
       if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN){
           SharedPreferences sPref;
           SharedPreferences.Editor ed;
           int HAFTA_SONI_TEK,HAFTA_SONI;
           long CREATE;
           Resources res;

        sPref = context.getSharedPreferences("informat", context.MODE_PRIVATE);
        ed=sPref.edit();
        HAFTA_SONI_TEK=sPref.getInt(SAVED_WEEK, 1);
        CREATE=sPref.getLong(SAVED_CREATE, 0);
        HAFTA_SONI=(int)((System.currentTimeMillis()-CREATE)/1000/60/60/24/7);
        res = context.getResources();
        if(HAFTA_SONI>40){
            HAFTA_SONI=40;
        }
           if(HAFTA_SONI<1){
               HAFTA_SONI=1;
           }
        int requestID=(int) System.currentTimeMillis();
        Intent firstIntent = new Intent(context,HomilaDavri.class);

           TaskStackBuilder stacBuilder= TaskStackBuilder.create(context);
            stacBuilder.addParentStack(HomilaDavri.class);
            stacBuilder.addNextIntent(firstIntent);
            Intent intente = new Intent(context, HomilaDavri.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intente, 0);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle(Integer.toString(HAFTA_SONI) + context.getString(R.string.homilaning_haftasi))
                    .setTicker(context.getString(R.string.app_name_main))
                    .setContentText(context.getString(R.string.homila_osib_bormoqda))
                    .setSmallIcon(R.drawable.minimain)
                    .setContentIntent(stacBuilder.getPendingIntent(requestID, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.app_icon))
                    .addAction(R.drawable.ic_check_black_24dp, context.getString(R.string.kozdan_kechirish),
                            pIntent).setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);;
            Notification notification = new Notification.BigPictureStyle(builder)
                    .bigPicture(
                            BitmapFactory.decodeResource(context.getResources(),
                                    Imagees[HAFTA_SONI-1])).build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);


        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, CREATE + (long) (HAFTA_SONI + 1) * (long) 7 * 24 * 60 * 60 * 1000, pendingIntent);
          ed.putInt(SAVED_WEEK, HAFTA_SONI);
       ed.apply();}

    }

}
