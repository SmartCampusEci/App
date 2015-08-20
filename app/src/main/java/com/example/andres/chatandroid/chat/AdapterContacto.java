package com.example.andres.chatandroid.chat;

import android.annotation.SuppressLint;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.andres.chatandroid.*;

import java.util.ArrayList;

/**
 * Created by andres on 08/08/2015.
 */

/*****
 * Clase encargada de adaptar las diferentes formas a los listview de contactos y busqueda de contactos, el listview de chat tiene un adaptador aparte
 * que cumple la misma funcion pero no tiene los separadores de la carrera
 */
public class AdapterContacto extends ArrayAdapter<Contacto> {

    private final Context context;
    private ArrayList<Contacto> itemsArrayList;
    private ArrayList<Contacto> tempItemsArrayList;
    private static LayoutInflater inflater = null;
    private String carrera = "";

    public AdapterContacto(Context context, ArrayList<Contacto> itemsArrayList) {

        super(context, R.layout.row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
        this.tempItemsArrayList = itemsArrayList;

    }

    /**
     * @param position poscision actual de la vista que se va a incluir en la lista
     * @param convertView
     * @param parent es el grupo de vistas creadas
     * @return view returna la respectiva vista segun pocision para mostrar en la pantalla
     *
     *
     * Este adaptador muestra los difernetes contactos con separadores segun la carrera
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemLayout = inflater.inflate(R.layout.main_list_item, parent, false);

        if (itemsArrayList.get(position).carne.equals("") && itemsArrayList.get(position).nombre.equals("")) {
            carrera = itemsArrayList.get(position).carrera;
            itemLayout = inflater.inflate(R.layout.main_list_item2, parent, false);
            TextView textCarrera = (TextView) itemLayout.findViewById(R.id.text2);
            textCarrera.setText(carrera);
            Log.i("count titulo, " + position, Integer.toString(itemsArrayList.size()));
        } else {

            //itemsArrayList.add(position-1, cont);
            Log.i("count llenado, " + position + " " + itemsArrayList.get(position).nombre, Integer.toString(itemsArrayList.size()));
            // 2. Get rowView from inflater

            itemLayout = inflater.inflate(R.layout.main_list_item, parent, false);
            // 3. Get the two text view from the rowView
            ViewHolder holder = new ViewHolder();
            itemLayout.setTag(holder);
            holder.text1 = (TextView) itemLayout.findViewById(R.id.text1);
            holder.text2 = (TextView) itemLayout.findViewById(R.id.text2);
            holder.textEmail = (TextView) itemLayout.findViewById(R.id.textEmail);

            holder.textEmail2 = (TextView) itemLayout.findViewById(R.id.textEmail2);
            holder.avatar = (ImageView) itemLayout.findViewById(R.id.avatar);

            // 4. Set the text for textView
            holder.text1.setText(itemsArrayList.get(position).nombre);
            holder.textEmail.setText(itemsArrayList.get(position).carne);
            holder.textEmail.setVisibility(View.GONE);
            holder.text2.setVisibility(View.VISIBLE);
            holder.text2.setText(itemsArrayList.get(position).carrera);
            holder.textEmail2.setText(itemsArrayList.get(position).semestre);
            FragmentContactos.photoCache.DisplayBitmap(requestPhoto(itemsArrayList.get(position).carne), holder.avatar);
        }
        return itemLayout;
    }


    /**
     * esta clase interna se usa para poder diferneciar los contactos de los separadores,
     * ademas de poder extraer la informacion de las vistas mas facilmente
     */
    public static class ViewHolder {
        public TextView text1;
        public TextView text2;
        public TextView textEmail;
        public TextView textEmail2;
        public ImageView avatar;
    }

    /**
     * mentodo encagado de cargar la uri de la imagen del contacto
     * @param email
     * @return
     */
    @SuppressLint("InlinedApi")
    private Uri requestPhoto(String email){
        Cursor emailCur = null;
        Uri uri = null;
        try{
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if(SDK_INT >= 11){
                String[] projection = { ContactsContract.CommonDataKinds.Email.PHOTO_URI };
                ContentResolver cr = context.getContentResolver();
                emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection,
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " = ?",
                        new String[]{email}, null);
                if (emailCur != null && emailCur.getCount() > 0) {
                    if (emailCur.moveToNext()) {
                        String photoUri = emailCur.getString( emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.PHOTO_URI));
                        if(photoUri != null)
                            uri = Uri.parse(photoUri);
                    }
                }
            }else if(SDK_INT < 11) {
                String[] projection = { ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
                ContentResolver cr = context.getContentResolver();
                emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        projection,
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " = ?",
                        new String[]{email}, null);
                if (emailCur.moveToNext()) {
                    int columnIndex = emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Photo.CONTACT_ID);
                    long contactId = emailCur.getLong(columnIndex);
                    uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                    uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(emailCur != null)
                    emailCur.close();
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return uri;
    }

}
