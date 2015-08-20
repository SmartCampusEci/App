package com.example.andres.chatandroid.chat;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.example.andres.chatandroid.R;
import com.example.andres.chatandroid.chat.*;
import com.example.andres.chatandroid.navigationDrawer.HeaderActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Clase encargada de recibir las notificaciones entrantes por parte de los servidores de GoogleCloudMenssagin
 */

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver
{
    private static final String TAG = "GcmBroadcastReceiver";
    private Context ctx;

    /**
     * Este metodo es el encargado de recibir los mensajes de los servidores de GoogleCloudMessaging
     * y diferenciar los tipos de mensaje
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("entor","algo");
        ctx = context;
        PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String messageType = gcm.getMessageType(intent);
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error", false);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server", false);
            } else {
                if(!intent.hasExtra("tipo"))
                    return;
                if(intent.getStringExtra("tipo").equals("CrearGrupo")){
                    String id = "grupo " + intent.getStringExtra("id");
                    String nombre= intent.getStringExtra("nombre");
                    //newFragment.show(, "AceptarSolicitud");


                }else {

                    String msg = intent.getStringExtra(DataProvider.COL_MESSAGE);
                    Log.i("msg", msg);
                    String senderEmail = intent.getStringExtra(DataProvider.COL_SENDER_EMAIL);
                    Log.i("senderEmail", senderEmail);
                    String receiverEmail = intent.getStringExtra(DataProvider.COL_RECEIVER_EMAIL);
                    ContentValues values = new ContentValues(2);
                    Log.i("Type", Integer.toString(DataProvider.MessageType.INCOMING.ordinal()));
                    values.put(DataProvider.COL_TYPE, DataProvider.MessageType.INCOMING.ordinal());
                    values.put(DataProvider.COL_MESSAGE, msg);
                    if(intent.getStringExtra("tipo").equals("Mensaje")){
                        values.put(DataProvider.COL_SENDER_EMAIL, senderEmail);
                    }else if(intent.getStringExtra("tipo").equals("Grupo")){
                        senderEmail = "grupo "+senderEmail;
                        values.put(DataProvider.COL_SENDER_EMAIL, senderEmail);
                        String remitente2 = intent.getStringExtra(DataProvider.COL_SENDER_EMAIL2);
                        values.put(DataProvider.COL_SENDER_EMAIL2, remitente2);
                    }

                    context.getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);
                    ContentValues value = new ContentValues(2);
                    value.put(DataProvider.COL_CHAT,1);
                    context.getContentResolver().update(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, senderEmail), value, null, null);
                    if (Common.isNotify()) {
                        sendNotification("New message", true);
                    }
                }
            }
            setResultCode(Activity.RESULT_OK);
        } finally {
            mWakeLock.release();
        }
    }

    /**
     * MEtodo encargao de mostrar la notificacion en la vista desplegable de notificaciones
     * @param text
     * @param launchApp
     */
    private void sendNotification(String text, boolean launchApp) {
        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(ctx);
        notification.setContentTitle(ctx.getString(R.string.app_name));
        notification.setContentText(text);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.abc_ab_share_pack_mtrl_alpha);
        if (!TextUtils.isEmpty(Common.getRingtone())) {
            notification.setSound(Uri.parse(Common.getRingtone()));
        }

        if (launchApp) {
            Intent intent = new Intent(ctx, HeaderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pi = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pi);
        }

        mNotificationManager.notify(1, notification.build());
    }
}

