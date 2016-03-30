package ca.etsmtl.applets.etsmobile.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.util.Calendar;

/**
 * Created by gnut3ll4 on 26/03/16.
 */
public class DailyListener implements WakefulIntentService.AlarmListener {

    public static final long INTERVAL_SYNC = AlarmManager.INTERVAL_HOUR;

    public void scheduleAlarms(AlarmManager mgr, PendingIntent pi, Context context) {
        // register when enabled in preferences

        Log.i("DailyListener", "Schedule update check...");

        Calendar calendar = Calendar.getInstance();
        mgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), INTERVAL_SYNC, pi);
    }

    public void sendWakefulWork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        // only when connected or while connecting...
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            boolean updateOnlyOnWifi = true;

            // if we have mobile or wifi connectivity...
            if (((netInfo.getType() == ConnectivityManager.TYPE_MOBILE) && updateOnlyOnWifi == false)
                    || (netInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                Log.d("DailyListener", "We have internet, start update check directly now!");

                Intent backgroundIntent = new Intent(context, BackgroundService.class);
                WakefulIntentService.sendWakefulWork(context, backgroundIntent);
            } else {
                Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");

                // enable receiver to schedule update when internet is available!
                ConnectivityReceiver.enableReceiver(context);
            }
        } else {
            Log.d("DailyListener", "We have no internet, enable ConnectivityReceiver!");

            // enable receiver to schedule update when internet is available!
            ConnectivityReceiver.enableReceiver(context);
        }
    }

    public long getMaxAge() {
        return (AlarmManager.INTERVAL_DAY + 60 * 1000);
    }
}