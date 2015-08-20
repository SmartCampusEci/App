package com.example.andres.chatandroid.chat;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andres.chatandroid.*;

/**
 * Created by andres on 03/07/2015.
 */
public class ChatActivity extends AppCompatActivity implements MessagesFragment.OnFragmentInteractionListener,
        EditContactDialog.OnFragmentInteractionListener, View.OnClickListener {

    private EditText msgEdit;
    private Button sendBtn;
    private String profileName;
    private String profileCarne;
    private String user;
    private String userSeleccion;
    private SolicitudesHTTP solicitud;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);


        user = getSharedPreferences(Constantes.sharePreference, Context.MODE_PRIVATE).getString(Constantes.PROPERTY_USER, "user");
        Log.i("user", user);
        userSeleccion = getIntent().getStringExtra(Constantes.PROPERTY_USER);
        msgEdit = (EditText) findViewById(R.id.msg_edit);
        sendBtn = (Button) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);

        solicitud = new SolicitudesHTTP();

        Cursor c = getContentResolver().query(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, userSeleccion), null, null, null, null);
        if (c.moveToFirst()) {
            profileName = c.getString(c.getColumnIndex(DataProvider.COL_NAME));
            profileCarne = c.getString(c.getColumnIndex(DataProvider.COL_CARNE));
            Log.i("nombre", profileName);
            Log.i("carne", profileCarne);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActualizarToolbar();
        setSupportActionBar(toolbar);

        registerReceiver(registrationStatusReceiver, new IntentFilter(Common.ACTION_REGISTER));

    }

    public void ActualizarToolbar(){
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.chat);
        toolbar.setTitle(profileName);
        toolbar.setSubtitle(profileCarne);
        toolbar.refreshDrawableState();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.send_btn:
                send(msgEdit.getText().toString());
                msgEdit.setText(null);
                break;
        }
    }

    @Override
    public void onEditContact(String name) {
      toolbar.setTitle(name);
    }

    @Override
    public String getProfileEmail() {
        return profileCarne;
    }

    private void send(final String txt) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";


                    Log.i("type", Integer.toString(DataProvider.MessageType.OUTGOING.ordinal()));
                    ContentValues values = new ContentValues(2);
                    values.put(DataProvider.COL_TYPE, DataProvider.MessageType.OUTGOING.ordinal());
                    values.put(DataProvider.COL_MESSAGE, txt);
                    values.put(DataProvider.COL_RECEIVER_EMAIL, profileCarne);
                    values.put(DataProvider.COL_SENDER_EMAIL,user);
                    getContentResolver().insert(DataProvider.CONTENT_URI_MESSAGES, values);


                    ContentValues value = new ContentValues(2);
                    value.put(DataProvider.COL_CHAT,1);
                    getContentResolver().update(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, profileCarne), value, null, null);
                    solicitud.send(user, profileCarne, txt);

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(msg)) {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onPause() {
        ContentValues values = new ContentValues(1);
        values.put(DataProvider.COL_COUNT, 0);
        getContentResolver().update(Uri.withAppendedPath(DataProvider.CONTENT_URI_PROFILE, userSeleccion), values, null, null);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(registrationStatusReceiver);
        //gcmUtil.cleanup();
        super.onDestroy();
    }

    private BroadcastReceiver registrationStatusReceiver = new  BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && Common.ACTION_REGISTER.equals(intent.getAction())) {
                switch (intent.getIntExtra(Common.EXTRA_STATUS, 100)) {
                    case Common.STATUS_SUCCESS:
                       toolbar.setSubtitle("online");
                        sendBtn.setEnabled(true);
                        break;

                    case Common.STATUS_FAILED:
                        toolbar.setSubtitle("offline");
                        break;
                }
            }
        }
    };

}
