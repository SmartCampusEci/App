package com.example.andres.chatandroid.chat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.andres.chatandroid.chat.*;

/**
 * Created by andres on 03/07/2015.
 */
public class Common extends Application {

    public static final String PROFILE_ID = "profile_id";
    public static final String ACTION_REGISTER = "com.example.andres.chatandroid.REGISTER";
    public static final String EXTRA_STATUS = "status";
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 0;
    public static String[] email_arr;
    private static SharedPreferences prefs;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        context = getApplicationContext();
    }

    public static boolean isNotify() {
        return prefs.getBoolean("notifications_new_message", true);
    }

    public static String getRingtone() {
        return prefs.getString("notifications_new_message_ringtone", android.provider.Settings.System.DEFAULT_NOTIFICATION_URI.toString());
    }

    public static String getServerUrl() {
        return Constantes.SERVER_URL; //prefs.getString("server_url_pref", Constantes.SERVER_URL);
    }

    public static String getSenderId() {
        return prefs.getString("sender_id_pref", Constantes.SENDER_ID);

    }

}
