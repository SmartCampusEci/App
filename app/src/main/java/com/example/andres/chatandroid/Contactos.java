package com.example.andres.chatandroid;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.andres.chatandroid.chat.*;
import org.json.*;
import java.util.ArrayList;

/**
 * Clase encargada de traer los contactos del servidor y seleccionar los que no tengan relacion con el usuario
 */
public class Contactos extends Activity implements  AdapterView.OnItemClickListener{

    private Button search;
    private EditText key;
    private ListView contactos;
    private AdapterContacto adapter;
    private ProgressDialog progreso;
    private ArrayList<Contacto> contactosBusqueda;
    private SharedPreferences pref;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        pref = getSharedPreferences(Constantes.sharePreference,MODE_PRIVATE);
        user = pref.getString(Constantes.PROPERTY_USER,"");

        search = (Button) findViewById(R.id.search);
        key = (EditText) findViewById(R.id.key);
        contactos = (ListView) findViewById(R.id.contactos);
        contactos.setOnItemClickListener(this);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!key.getText().toString().equals(null) && !key.getText().toString().equals("")){
                    progreso = new ProgressDialog(Contactos.this);
                    progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progreso.setMessage("Un momento por favor...");
                    progreso.setCancelable(true);
                    progreso.setMax(100);

                    traerContactos contact = new traerContactos(Contactos.this);
                    contact.execute(key.getText().toString());

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contactos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Este metodo se encarga de abrir la ventana de chat relacionada al usuario que se le da click
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AdapterContacto.ViewHolder holder = (AdapterContacto.ViewHolder)view.getTag();

        ContentValues values = new ContentValues(2);

        values.put(DataProvider.COL_NAME, holder.text1.getText().toString());
        values.put(DataProvider.COL_CARRERA, holder.text2.getText().toString());
        values.put(DataProvider.COL_CARNE, holder.textEmail.getText().toString());
        values.put(DataProvider.COL_SEMESTRE,"Semestre " + holder.textEmail2.getText().toString());
        values.put(DataProvider.COL_CHAT, 0);
        Contactos.this.getContentResolver().insert(DataProvider.CONTENT_URI_PROFILE, values);

    }

    /*********************************************************************************************/

    /**
     * esta clase esta se contruyo debido a que desde hace unas cuantas versiones de android ya no se permite ejecutar
     * elementos soa y rest en hilos principales
     */
    private class traerContactos extends AsyncTask<String, Integer, Boolean> {

        Context context;
        JSONArray json;

        public traerContactos(Context context){
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean result = false;

            SolicitudesHTTP solicitudpost = new SolicitudesHTTP();

            json = solicitudpost.GetContactos(params[0]);

            if(json.length() > 0)
                result = true;

            return result;
        }

        @Override
        protected void onPreExecute() {
            progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    traerContactos.this.cancel(true);
                }
            });
            progreso.setProgress(0);
            progreso.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){

                contactosBusqueda = new ArrayList<>();
                for(int i = 0; i<json.length(); i++){
                    try {

                        Contacto contact = new Contacto();
                        if(i==0 || !json.getJSONObject(i).getString("carrera").equals(json.getJSONObject(i-1).getString("carrera"))){
                            contact.nombre = "";
                            contact.carne ="";
                            contact.carrera =  json.getJSONObject(i).getString("carrera");
                            contactosBusqueda.add(contact);
                            contact = new Contacto();
                        }
                        JSONObject objeto = json.getJSONObject(i);
                        Cursor c = Contactos.this.getContentResolver().query(
                                com.example.andres.chatandroid.chat.DataProvider.CONTENT_URI_PROFILE,
                                new String[]{DataProvider.COL_IDENTIFICACION, DataProvider.COL_CARNE, DataProvider.COL_NAME, DataProvider.COL_COUNT},
                                DataProvider.COL_CARNE +"=?",
                                new String[]{objeto.getString("carne")},
                                null);

                        if(!c.moveToFirst() && !objeto.getString("carne").equals(user)){
                            contact.nombre = objeto.getString("nombre");
                            contact.carne = objeto.getString("carne");
                            contact.carrera = objeto.getString("carrera");
                            contact.semestre = objeto.getString("semestre");
                            contactosBusqueda.add(contact);
                            Log.i("nombre",contact.nombre);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                adapter = new AdapterContacto(context, contactosBusqueda);
                contactos.setAdapter(adapter);

                Toast.makeText(context, "Busqueda Finalizada", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "No hay contactos relacionados a la busqueda", Toast.LENGTH_SHORT).show();
            }

            progreso.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progreso.setProgress(1);
        }
    }
}