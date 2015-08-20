package com.example.andres.chatandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.andres.chatandroid.chat.Constantes;
import com.example.andres.chatandroid.chat.SolicitudesHTTP;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class registro extends Activity {

    private Context context;

    private Spinner carreras;
    private Spinner semestre;
    private EditText identificacion;
    private EditText carne;
    private EditText nombre;
    private EditText email;
    private EditText password;
    private Button registro;
    private ProgressDialog progreso;
    private HashMap<String, String> dictianaryCarreras;
    private SharedPreferences pref;

    private SolicitudesHTTP solicitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        solicitud = new SolicitudesHTTP();

        carne = (EditText) findViewById(R.id.carne);
        nombre = (EditText) findViewById(R.id.nombre);
        email = (EditText) findViewById(R.id.email);
        password =(EditText) findViewById(R.id.password);
        registro = (Button) findViewById(R.id.registrar);
        carreras = (Spinner) findViewById(R.id.carreras);
        dictianaryCarreras = new HashMap<>();
        identificacion = (EditText) findViewById(R.id.identificacion);
        semestre = (Spinner) findViewById(R.id.semestre);
        pref = getSharedPreferences(Constantes.sharePreference,Context.MODE_PRIVATE);
        Bundle bundle = this.getIntent().getExtras();

        final String carrera = bundle.getString("CARRERAS");

        System.out.println("Llgo: " + carrera);

        JSONArray array = new JSONArray();

        try {
            array = new JSONArray(carrera);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Armar:","Lista");

        List<String> lista = new LinkedList<String>();

        for(int i = 0; i<array.length(); i++)
            try {
                String id = array.getJSONObject(i).getString("id");
                String nombre =  array.getJSONObject(i).getString("nombre");
                dictianaryCarreras.put(nombre, id);
                lista.add(nombre);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        System.out.println(lista.size());
        Log.i("Spinner:", "Carreras");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(com.example.andres.chatandroid.registro.this,  android.R.layout.simple_spinner_item, lista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carreras.setAdapter(adapter);
        Log.i("Spinner:","Semestre");
        //Creamos el adaptador
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(com.example.andres.chatandroid.registro.this,R.array.semestre,android.R.layout.simple_spinner_item);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Le indicamos al spinner el adaptador a usar
        semestre.setAdapter(adapter2);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progreso = new ProgressDialog(com.example.andres.chatandroid.registro.this);
                progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progreso.setMessage("Un momento por favor...");
                progreso.setCancelable(true);
                progreso.setMax(100);

                String carreraSeleccionada = dictianaryCarreras.get(carreras.getSelectedItem().toString());
                String semestreselect = semestre.getSelectedItem().toString();
                Log.i("Semestre", semestreselect);
                Log.i("IdCarrera", carreraSeleccionada);

                TareaRegistro tarea = new TareaRegistro(com.example.andres.chatandroid.registro.this);
                tarea.execute(identificacion.getText().toString(), carne.getText().toString(), nombre.getText().toString(), email.getText().toString(), password.getText().toString(), carreraSeleccionada, semestreselect);
            }
        });
    }

    private class TareaRegistro extends AsyncTask<String, Integer, Boolean> {

        private Context context;
        private String user;
        private String nombre;

        public TareaRegistro(Context context){
            this.context = context;
        }


        @Override
        protected Boolean doInBackground(String... params) {
            boolean registrado = false;

            Log.i("Carrera",params[5]);
                //Nos registramos en nuestro servidor
            registrado = solicitud.registroServidor(params[0], params[1], params[2], params[3], params[4], params[5], params[6]);
            user = params[1];
            nombre = params[2];

            return registrado;
        }

        @Override
        protected void onPreExecute() {
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    TareaRegistro.this.cancel(true);
                }
            });
            progreso.setProgress(0);
            progreso.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constantes.PROPERTY_USER, carne.toString());
                editor.putString(Constantes.PROPERTY_NAME, nombre.toString());
                editor.commit();
                        Toast.makeText(com.example.andres.chatandroid.registro.this, "Registro completo", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(context,login.class);
                startActivity(i);
            }else{
                Toast.makeText(com.example.andres.chatandroid.registro.this, "Problemas de conexion con el servidor", Toast.LENGTH_SHORT).show();
            }
            progreso.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progreso.setProgress(1);
        }

    }
}
