package com.example.andres.chatandroid.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andres on 04/07/2015.
 */

/**
 * Clase encaragada de realizar el registro del dispositivo en los servidores de GoogleCloudMessaging
 * ademas de guardar esa misma informacion mas la del usuario en preferencias del sistema para su posterior identificacion.
 */
public class RegistroDispositivoEnGcm {

    private Context ctx;

    public RegistroDispositivoEnGcm(Context ctx){
        this.ctx = ctx;
    }

    /**
     * Registra el dispositivo en los servidores de GoogleCloudMessaging y devuelve el id generado por este
     * @param prefs
     * @return
     */
    public String getRegistrationId(SharedPreferences prefs) {

        String registrationId = prefs.getString(Constantes.PROPERTY_REG_ID, "");

        if (registrationId.length() == 0) {
            Log.d(Constantes.TAG, "Registro GCM no encontrado.");
            return "";
        }

        String registeredUser =
                prefs.getString(Constantes.PROPERTY_USER, "user");

        int registeredVersion =
                prefs.getInt(Constantes.PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        long expirationTime =
                prefs.getLong(Constantes.PROPERTY_EXPIRATION_TIME, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));

        Log.d(Constantes.TAG, "Registro GCM encontrado (usuario=" + registeredUser +
                ", version=" + registeredVersion +
                ", expira=" + expirationDate + ")");

        int currentVersion = getAppVersion();

        if (registeredVersion != currentVersion) {
            Log.d(Constantes.TAG, "Nueva versión de la aplicación.");
            return "";
        } else if (System.currentTimeMillis() > expirationTime) {
            Log.d(Constantes.TAG, "Registro GCM expirado.");
            return "";
        }

        return registrationId;
    }

    private int getAppVersion() {
        try {
            PackageInfo packageInfo = ctx.getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);

            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Error al obtener versión: " + e);
        }
    }


    /**
     * Registra la informacin obtenida por los servidores de google en las preferencias del sistema para su posterior identificacion
     * @param prefs
     * @param user
     * @param regId
     * @param pass
     */
    public void setRegistrationId(SharedPreferences prefs,String user, String regId, String pass) {

        int appVersion = getAppVersion();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constantes.PROPERTY_USER, user);
        editor.putString(Constantes.PROPERTY_PASS, pass);
        editor.putString(Constantes.PROPERTY_REG_ID, regId);
        editor.putInt(Constantes.PROPERTY_APP_VERSION, appVersion);
        editor.putLong(Constantes.PROPERTY_EXPIRATION_TIME,
                System.currentTimeMillis() + Constantes.EXPIRATION_TIME_MS);

        editor.commit();
    }

    public void broadcastStatus(boolean status) {
        Intent intent = new Intent(Common.ACTION_REGISTER);
        intent.putExtra(Common.EXTRA_STATUS, status ? Common.STATUS_SUCCESS : Common.STATUS_FAILED);
        ctx.sendBroadcast(intent);
    }
}
