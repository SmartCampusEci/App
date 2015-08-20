package com.example.andres.chatandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.andres.chatandroid.chat.ChatActivity;
import com.example.andres.chatandroid.chat.Constantes;
import com.example.andres.chatandroid.chat.DataProvider;
import com.example.andres.chatandroid.chat.PhotoCache;

/**
 * Este fragmento tiene el objetivo de mostrar los contactos relacionados con los cuales se tiene un
 * chat activo con el usuario y se cargan con array adapter
 */

public class FragmentMensages extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener  {

    ListView listView;
    private ContactCursorAdapter ContactCursorAdapter;
    public static PhotoCache photoCache;

    public FragmentMensages() {
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
        ContactCursorAdapter = new ContactCursorAdapter(this.getActivity(), null);
        listView.setAdapter(ContactCursorAdapter);
        photoCache = new PhotoCache(this.getActivity());

        getLoaderManager().initLoader(0, null, this).toString();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
        ViewHolder viewholder = (ViewHolder) view.getTag();

        String usuarioSeleccion = viewholder.textEmail.getText().toString();

        Intent intent = new Intent(this.getActivity(), ChatActivity.class);
        intent.putExtra(Constantes.PROPERTY_USER, usuarioSeleccion);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.i("URI",com.example.andres.chatandroid.chat.DataProvider.CONTENT_URI_PROFILE.toString());
        Log.i("activity", getActivity().toString());
        CursorLoader loader = new CursorLoader(getActivity(),
                com.example.andres.chatandroid.chat.DataProvider.CONTENT_URI_PROFILE,
                new String[]{DataProvider.COL_IDENTIFICACION, DataProvider.COL_CARNE, DataProvider.COL_NAME, DataProvider.COL_COUNT, DataProvider.COL_CARRERA},
                "chat=?",
                new String[]{Integer.toString(1)},
                DataProvider.COL_NAME + " ASC");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        ContactCursorAdapter.swapCursor(arg1);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        ContactCursorAdapter.swapCursor(null);
    }

    /*********************************************************************************/
    public class ContactCursorAdapter extends CursorAdapter {

        private LayoutInflater mInflater;
        private String carrera = "";

        public ContactCursorAdapter(Context context, Cursor c) {
            super(context, c, 0);
        }

        @Override
        public int getCount() {
            return getCursor() == null ? 0 : super.getCount();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View itemLayout ;

                itemLayout = LayoutInflater.from(context).inflate(R.layout.main_list_item, parent, false);
                ViewHolder holder = new ViewHolder();
                itemLayout.setTag(holder);
                holder.text1 = (TextView) itemLayout.findViewById(R.id.text1);
                holder.text2 = (TextView) itemLayout.findViewById(R.id.text2);
                holder.textEmail = (TextView) itemLayout.findViewById(R.id.textEmail);
                holder.avatar = (ImageView) itemLayout.findViewById(R.id.avatar);
                return itemLayout;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

                ViewHolder holder = (ViewHolder) view.getTag();
                holder.text1.setText(cursor.getString(cursor.getColumnIndex(DataProvider.COL_NAME)));
                holder.textEmail.setText(cursor.getString(cursor.getColumnIndex(DataProvider.COL_CARNE)));
                int count = cursor.getInt(cursor.getColumnIndex(DataProvider.COL_COUNT));
                Log.i("Cant mensajes", Integer.toString(count));
                if (count > 0) {
                    holder.text2.setVisibility(View.VISIBLE);
                    holder.text2.setText(String.format("%d new message%s", count, count == 1 ? "" : "s"));
                } else
                    holder.text2.setVisibility(View.GONE);

                photoCache.DisplayBitmap(requestPhoto(cursor.getString(cursor.getColumnIndex(DataProvider.COL_IDENTIFICACION))), holder.avatar);
        }
    }

    private static class ViewHolder {
        TextView text1;
        TextView text2;
        TextView textEmail;
        ImageView avatar;
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
