package ca.etsmtl.applets.etsmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.j256.ormlite.dao.Dao;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ca.etsmtl.applets.etsmobile.db.DatabaseHelper;
import ca.etsmtl.applets.etsmobile.http.DataManager;
import ca.etsmtl.applets.etsmobile.http.MonETSNotificationsRequest;
import ca.etsmtl.applets.etsmobile.model.MonETSNotification;
import ca.etsmtl.applets.etsmobile.model.MonETSNotificationList;

public class Utility {

    public static boolean isTabletDevice(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

    }

    public static boolean isNetworkAvailable(final Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Return Date from string "yyyy-MM-dd"
     *
     * @param dateString
     * @return
     */
    public static Date getDateFromString(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getStringForApplETSApiFromDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        return simpleDateFormat.format(date);
    }

    public static Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (cookieHeader != null) {
            String[] cookiesRaw = cookieHeader.split("; ");
            for (int i = 0; i < cookiesRaw.length; i++) {
                String[] parts = cookiesRaw[i].split("=", 2);
                String value = parts.length > 1 ? parts[1] : "";
                if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                result.put(parts[0], value);
            }
        }
        return result;
    }

    public static Date getDate(final SecurePreferences prefs, final String key, final Date defValue) {
        if (!prefs.contains(key + "_value")) {
            return defValue;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(prefs.getLong(key + "_value", 0));
        return calendar.getTime();
    }

    public static void putDate(final SecurePreferences prefs, final String key, final Date date) {
        prefs.edit().putLong(key + "_value", date.getTime()).commit();
    }

    public static void saveCookieExpirationDate(String cookie, SecurePreferences securePreferences) {

        Map<String, String> parsedCookie = Utility.parseCookies(cookie);
        String expires = parsedCookie.get("expires");

        Date expirationDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            expirationDate = df.parse(expires);
            Utility.putDate(securePreferences, Constants.EXP_DATE_COOKIE, expirationDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void loadNotifications(Context context, final RequestListener<Object> requestListener) {
        final SecurePreferences securePreferences = new SecurePreferences(context);
        final boolean allNotifsLoaded = securePreferences.getBoolean(Constants.ALL_NOTIFS_LOADED, false);
        MonETSNotificationsRequest monETSNotificationsRequest;
        if (!allNotifsLoaded) {
            monETSNotificationsRequest = new MonETSNotificationsRequest(context, false);
        } else {
            monETSNotificationsRequest = new MonETSNotificationsRequest(context, true);
        }

        DataManager dataManager = DataManager.getInstance(context);
        final DatabaseHelper databaseHelper = new DatabaseHelper(context);

        dataManager.sendRequest(monETSNotificationsRequest, new RequestListener<Object>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                requestListener.onRequestFailure(spiceException);
            }

            @Override
            public void onRequestSuccess(Object o) {
                if (o instanceof MonETSNotificationList) {
                    try {
                        Dao<MonETSNotification, ?> dao = databaseHelper.getDao(MonETSNotification.class);
                        MonETSNotificationList list = (MonETSNotificationList) o;
                        for (MonETSNotification monETSNotification : list) {
                            dao.createOrUpdate(monETSNotification);
                        }
                        if (!allNotifsLoaded) {
                            securePreferences.edit().putBoolean(Constants.ALL_NOTIFS_LOADED, true).commit();
                        }
                        requestListener.onRequestSuccess(list);
                    } catch (SQLException e) {
                        e.printStackTrace();

                    }
                }

            }
        });
    }

}
