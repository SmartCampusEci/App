package com.example.andres.chatandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.andres.chatandroid.chat.AdapterContacto;
import com.example.andres.chatandroid.chat.ChatActivity;
import com.example.andres.chatandroid.chat.Constantes;
import com.example.andres.chatandroid.chat.Contacto;
import com.example.andres.chatandroid.chat.DataProvider;
import com.example.andres.chatandroid.chat.PhotoCache;

import java.util.ArrayList;
import java.util.Objects;

/**
 * este fragmento tiene el objetivo de mostrar los contactos relacionado con el usuario y se cargan con array adapter
 */
public class FragmentContactos extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener  {

    ListView listView;
    private AdapterContacto ContactCursorAdapter;
    public static PhotoCache photoCache;

    public FragmentContactos() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        listView = (ListView) view.findViewById(R.id.contactslist);

        listView.setOnItemClickListener(this);

        onCreateLoader(0,new Bundle());

        photoCache = new PhotoCache(this.getActivity());

        getLoaderManager().initLoader(0, null, this).toString();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void generarListaDeContactos(Cursor c){

        ArrayList<Contacto> contactos = new ArrayList<Contacto>();

        while(c.moveToNext()){
            Contacto contact = new Contacto();
            if(c.isFirst()) {
                contact.nombre = "";
                contact.carne = "";
                contact.carrera = c.getString(c.getColumnIndex(DataProvider.COL_CARRERA));
                contactos.add(contact);
                contact = new Contacto();
            }else{
                String carrera1 = c.getString(c.getColumnIndex(DataProvider.COL_CARRERA));
                c.moveToPrevious();
                String carrera2 = c.getString(c.getColumnIndex(DataProvider.COL_CARRERA));
                c.moveToNext();
                if(!carrera1.equals(carrera2)){
                    contact.nombre = "";
                    contact.carne = "";
                    contact.carrera = c.getString(c.getColumnIndex(DataProvider.COL_CARRERA));
                    contactos.add(contact);
                    contact = new Contacto();
                }
            }


            contact.nombre =c.getString(c.getColumnIndex(DataProvider.COL_NAME));
            contact.carne =c.getString(c.getColumnIndex(DataProvider.COL_CARNE));
            contact.semestre =c.getString(c.getColumnIndex(DataProvider.COL_SEMESTRE));
            contact.carrera =c.getString(c.getColumnIndex(DataProvider.COL_CARRERA));
            contactos.add(contact);
        }

        ContactCursorAdapter = new AdapterContacto(getActivity(),contactos);
        listView.setAdapter(ContactCursorAdapter);

    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
        if(view.getTag() instanceof  AdapterContacto.ViewHolder){
            AdapterContacto.ViewHolder viewholder = (AdapterContacto.ViewHolder) view.getTag();

            String usuarioSeleccion = viewholder.textEmail.getText().toString();

            Intent intent = new Intent(this.getActivity(), ChatActivity.class);
            intent.putExtra(Constantes.PROPERTY_USER, usuarioSeleccion);
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.i("URI",com.example.andres.chatandroid.chat.DataProvider.CONTENT_URI_PROFILE.toString());
        Log.i("activity", getActivity().toString());
        CursorLoader loader = new CursorLoader(getActivity(),
                com.example.andres.chatandroid.chat.DataProvider.CONTENT_URI_PROFILE,
                new String[]{DataProvider.COL_IDENTIFICACION, DataProvider.COL_CARNE, DataProvider.COL_NAME, DataProvider.COL_COUNT, DataProvider.COL_CARRERA, DataProvider.COL_SEMESTRE},
                null,
                null,
                DataProvider.COL_CARRERA+ ", " + DataProvider.COL_NAME + " ASC");

        generarListaDeContactos(loader.loadInBackground());

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        //ContactCursorAdapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        //ContactCursorAdapter.swapCursor(null);
    }
    /*******************************************************************************************/

    @SuppressLint("InlinedApi")
    private Uri requestPhoto(String email) {
        Cursor emailCur = null;
        Uri uri = null;
        try {
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT >= 11) {
                String[] projection = {ContactsContract.CommonDataKinds.Email.PHOTO_URI};
                ContentResolver cr = this.getActivity().getContentResolver();
                emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection,
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " = ?",
                        new String[]{email}, null);
                if (emailCur != null && emailCur.getCount() > 0) {
                    if (emailCur.moveToNext()) {
                        String photoUri = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.PHOTO_URI));
                        if (photoUri != null)
                            uri = Uri.parse(photoUri);
                    }
                }
            } else if (SDK_INT < 11) {
                String[] projection = {ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
                ContentResolver cr = this.getActivity().getContentResolver();
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (emailCur != null)
                    emailCur.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return uri;
    }

}
