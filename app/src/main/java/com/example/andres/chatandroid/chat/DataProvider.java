package com.example.andres.chatandroid.chat;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by andres on 03/07/2015.
 */

/**
 * Esta clas eesta encargada de realizar los crud de la diferenete informacion de las bases de datos
 */
public class DataProvider extends ContentProvider {

    /**
     * Estas uri se usan para diferenciar las tablas de las bases de datos
     */
        public static final Uri CONTENT_URI_MESSAGES = Uri.parse("content://com.example.andres.chatandroid.provider/messages");
        public static final Uri CONTENT_URI_PROFILE = Uri.parse("content://com.example.andres.chatandroid.provider/profile");

    /**
     * Con este tipo de dato reconocemos si el mensaje es de envio o de entrada
     */
        public enum MessageType {

            INCOMING, OUTGOING
        }

    /**
     * con estas variables realizamos la creacion de las tablas y ademas las usamos en las difernets consultas en la aplicacion
     */
        //parameters recognized by demo server
        public static final String SENDER_EMAIL 		= "senderEmail";
        public static final String RECEIVER_EMAIL 		= "receiverEmail";
        public static final String REG_ID 				= "regId";
        public static final String MESSAGE 				= "message";

        // TABLE MESSAGE
        public static final String TABLE_MESSAGES 		= "messages";
        public static final String COL_ID 	            = "_id";
        public static final String COL_TYPE				= "type";
        public static final String COL_SENDER_EMAIL 	= "remitente";
        public static final String COL_SENDER_EMAIL2 	= "remitente2";
        public static final String COL_RECEIVER_EMAIL 	= "destinatario";
        public static final String COL_MESSAGE 			= "msg";
        public static final String COL_TIME 			= "tiempo";


        // TABLE PROFILE
        public static final String TABLE_PROFILE        = "profile";
        public static final String COL_IDENTIFICACION	= "_id";
        public static final String COL_CARNE	        = "carne";
        public static final String COL_NAME	            = "nombre";
        public static final String COL_COUNT            = "count";
        public static final String COL_CARRERA          = "carrera";
        public static final String COL_SEMESTRE         = "semestre";
        public static final String COL_CHAT          	= "chat";

        private DbHelper dbHelper;

    /**
     * Con estas variables diferenciamos si son uno o varios los datos que debemos consultar o modificar
     */
        private static final int MESSAGES_ALLROWS = 1;
        private static final int MESSAGES_SINGLE_ROW = 2;
        private static final int PROFILE_ALLROWS = 3;
        private static final int PROFILE_SINGLE_ROW = 4;

        private static final UriMatcher uriMatcher;
        static {
            uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
            uriMatcher.addURI("com.example.andres.chatandroid.provider", "messages", MESSAGES_ALLROWS);
            uriMatcher.addURI("com.example.andres.chatandroid.provider", "messages/#", MESSAGES_SINGLE_ROW);
            uriMatcher.addURI("com.example.andres.chatandroid.provider", "profile", PROFILE_ALLROWS);
            uriMatcher.addURI("com.example.andres.chatandroid.provider", "profile/*", PROFILE_SINGLE_ROW);
        }

        @Override
        public boolean onCreate() {
            dbHelper = new DbHelper(getContext());
            return true;
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String groupby = null;

            Log.i("URI", uri.toString());

            switch(uriMatcher.match(uri)) {
                case MESSAGES_ALLROWS:
                    qb.setTables(TABLE_MESSAGES);
                    break;

                case MESSAGES_SINGLE_ROW:
                    qb.setTables(TABLE_MESSAGES);
                    qb.appendWhere("_id = " + uri.getLastPathSegment());
                    break;

                case PROFILE_ALLROWS:
                    qb.setTables(TABLE_PROFILE);
                    break;

                case PROFILE_SINGLE_ROW:
                    qb.setTables(TABLE_PROFILE);
                    Log.i("URIlast", uri.getLastPathSegment());
                    qb.appendWhere("carne = " + "'" +uri.getLastPathSegment().toString() + "'");
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }

            if(selection != null && selection.contains("GROUP BY")){
                groupby = selection.split("GROUP BY")[1];
                selection = selection.split("GROUP BY")[0];
            }


            Cursor c = qb.query(db, projection, selection, selectionArgs, groupby, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        }

        @Override
        public Uri insert(Uri uri, ContentValues values) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            long id;
            switch(uriMatcher.match(uri)) {
                case MESSAGES_ALLROWS:
                    id = db.insertOrThrow(TABLE_MESSAGES, null, values);
                    if (values.get(COL_RECEIVER_EMAIL) == null) {
                        db.execSQL("update profile set count = count+1 where carne = ?", new Object[]{values.get(COL_SENDER_EMAIL)});
                        getContext().getContentResolver().notifyChange(CONTENT_URI_PROFILE, null);
                    }
                    break;

                case PROFILE_ALLROWS:
                    id = db.insertOrThrow(TABLE_PROFILE, null, values);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }

            Uri insertUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(insertUri, null);
            return insertUri;
        }

        @Override
        public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            int count;
            switch(uriMatcher.match(uri)) {
                case MESSAGES_ALLROWS:
                    count = db.update(TABLE_MESSAGES, values, selection, selectionArgs);
                    break;

                case MESSAGES_SINGLE_ROW:
                    count = db.update(TABLE_MESSAGES, values, "_id = ?", new String[]{uri.getLastPathSegment()});
                    break;

                case PROFILE_ALLROWS:
                    count = db.update(TABLE_PROFILE, values, selection, selectionArgs);
                    break;

                case PROFILE_SINGLE_ROW:
                    count = db.update(TABLE_PROFILE, values, "carne = ?", new String[]{uri.getLastPathSegment()});
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            int count;
            switch(uriMatcher.match(uri)) {
                case MESSAGES_ALLROWS:
                    count = db.delete(TABLE_MESSAGES, selection, selectionArgs);
                    break;

                case MESSAGES_SINGLE_ROW:
                    count = db.delete(TABLE_MESSAGES, "_id = ?", new String[]{uri.getLastPathSegment()});
                    break;

                case PROFILE_ALLROWS:
                    count = db.delete(TABLE_PROFILE, selection, selectionArgs);
                    break;

                case PROFILE_SINGLE_ROW:
                    count = db.delete(TABLE_PROFILE, "carne = ?", new String[]{uri.getLastPathSegment()});
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }

        @Override
        public String getType(Uri uri) {
            return null;
        }

    /**
     * Esta clase nos ayuda a realziar la creacion y el uso de las bases de datos (SQLite)
     */
        private static class DbHelper extends SQLiteOpenHelper {
            private static final String DATABASE_NAME = "chatandroid.db";
            private static final int DATABASE_VERSION = 10;
            public DbHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("create table messages ("
                        + "_id integer primary key autoincrement, "
                        + COL_TYPE        	  +" integer, " //se usa con el fin de saber si es un mensaje de entrada o salida
                        + COL_MESSAGE         +" text, "    //se usa para alamacenar los emnsajes ya sean de salida o entrada
                        + COL_SENDER_EMAIL 	  +" text, "    //Se usa con el fin de guarda cual es el usuario que envia el menaje
                        + COL_SENDER_EMAIL2   +" text, "    //este campo es exclusivo en caso de que el chat sea grupal, y se usa en conjunto con el anterior en el cual se guarda el nombre del grupo que envia el mensaje y en este se guarda el usuario que lo envia
                        + COL_RECEIVER_EMAIL  +" text, "    //este campo se usa para guardar quien recive el mensaje
                        + COL_TIME 			  +" datetime default current_timestamp);");//en este campo se almacena el momento exacto de envio del mensaje

                db.execSQL("create table profile("
                        + "_id integer primary key autoincrement , "
                        + COL_CARNE                +" text unique, " //Se alamacena el carne del usuario con el cual se tiene contacto
                        + COL_NAME                 +" text, " //Se almacena el nombre del usuario con el cual se tiene contacto
                        + COL_CARRERA              +" text, " //Se alamacena la carrera del usuario con la cual se tiene contacto
                        + COL_SEMESTRE             +" text, " //Se alamacena el semestre en el que va el usuario con el que se tiene contacto
                        + COL_CHAT                 +" integer, "//Este campo se usa con el fin de saber si se tiene un chat activo con el usuario en caso de tenerlo y eleminar el chat se debe cambiar el valor de este campo (1 = chat activo, 0 = chat inactivo)
                        + COL_COUNT                +" integer default 0);");//se usa con el fin de contar los mensajes entrantes del usuario
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        }

}
