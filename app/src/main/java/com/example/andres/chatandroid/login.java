package com.example.andres.chatandroid;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.example.andres.chatandroid.chat.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;

import java.io.IOException;


public class login extends Activity {

    private Button Registrar;
    private Button login;
    private EditText username;
    private EditText password;

    private String regid;
    private GoogleCloudMessaging gcm;

    private Context context;

    private SolicitudesHTTP solicitud;
    private RegistroDispositivoEnGcm registrogcm;
    private SharedPreferences preferences;

    private ProgressDialog progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Registrar = (Button) findViewById(R.id.registro);

        login = (Button) findViewById(R.id.login);

        username = (EditText)findViewById(R.id.username);
        password =  (EditText)findViewById(R.id.pass);

        context = getApplicationContext();

        gcm = GoogleCloudMessaging.getInstance(context);

        preferences = getSharedPreferences(Constantes.sharePreference, context.MODE_PRIVATE);

        registrogcm = new RegistroDispositivoEnGcm(context);

        solicitud = new SolicitudesHTTP();

        Registrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progreso = new ProgressDialog(login.this);
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Un momento por favor...");
                progreso.setCancelable(true);
                progreso.setMax(100);

                traerCarrera carrera = new traerCarrera(login.this);
                carrera.execute();
            }
        });



        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                progreso = new ProgressDialog(login.this);
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Un momento por favor...");
                progreso.setCancelable(true);
                progreso.setMax(100);

                context = getApplicationContext();

                //Obtenemos el Registration ID guardado

                regid = registrogcm.getRegistrationId(preferences);

                inicioSesion inicio = new inicioSesion(login.this);
                inicio.execute(username.getText().toString().trim(), password.getText().toString());

            }
        });
    }




    private class traerCarrera extends AsyncTask<Void, Integer, Boolean> {
        private Context context;
        private JSONArray jsonArray;

        public traerCarrera(Context context){
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params){
            boolean respuesta = false;

            try {
                SolicitudesHTTP solicitud = new SolicitudesHTTP();
                jsonArray = solicitud.Get();

                Log.i("JsonArray", jsonArray.toString());

                if(!jsonArray.toString().equals(null) && jsonArray.length()>0){
                    Log.i("JsonArray",jsonArray.toString());
                    respuesta = true;
                }


            publishProgress(1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPreExecute() {
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    traerCarrera.this.cancel(true);
                }
            });
            progreso.setProgress(0);
            progreso.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){

                Toast.makeText(login.this, "Tarea Finalizada", Toast.LENGTH_SHORT).show();

                Intent i=new Intent(context,registro.class);

                //Creamos la informaci�n a pasar entre actividades
                Bundle b = new Bundle();
                Log.i("JsonArray", jsonArray.toString());
                b.putString("CARRERAS", jsonArray.toString());

                //A�adimos la informaci�n al intent
                i.putExtras(b);

                startActivity(i);
            }else{
                Toast.makeText(login.this, "Problemas de conexion con el servidor intentelo mas tarde", Toast.LENGTH_SHORT).show();
            }
            progreso.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progreso.setProgress(1);
        }
    }

    private class inicioSesion extends AsyncTask<String, Integer, Boolean> {
        private Context context;
        private String user;
        private String pass;
        public inicioSesion(Context context){
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params){
            boolean respuesta = false;

            try {



                    gcm = GoogleCloudMessaging.getInstance(context);


                    regid = gcm.register(Constantes.SENDER_ID);
                    Log.i("regid",regid);

                user = params[0];
                pass = params[1];
                Log.d(Constantes.TAG, "Registrado en GCM: registration_id=" + regid);
                Log.d(Constantes.TAG, "user=" + params[0]);

                //Guardamos los datos del registro


                SolicitudesHTTP solicitud = new SolicitudesHTTP();
                respuesta = solicitud.inicioSesion(params[0], params[1], regid);

                publishProgress(1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return respuesta;
        }


        @Override
        protected void onPreExecute() {
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    inicioSesion.this.cancel(true);
                }
            });
            progreso.setProgress(0);
            progreso.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                registrogcm.setRegistrationId(preferences, user, regid, pass);
                registrogcm.broadcastStatus(true);
                Toast.makeText(login.this, "Inicio de sesion completo", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(context,com.example.andres.chatandroid.navigationDrawer.HeaderActivity.class);
                startActivity(i);

            }else{
                Toast.makeText(login.this, "Problemas de conexion con el servidor intentelo mas tarde", Toast.LENGTH_SHORT).show();
            }
            progreso.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progreso.setProgress(1);
        }
    }
}
