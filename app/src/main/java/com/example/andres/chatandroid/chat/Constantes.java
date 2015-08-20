package com.example.andres.chatandroid.chat;

/**
 * Created by andres on 03/07/2015.
 */

/**
 * clase que mantiene datos que se usan repetidamente en toda la aplicacion.
 */
public class Constantes {

    public static final String SERVER_URL = "http://proyectopgr.herokuapp.com/rest/servergcm";
    public static final String SENDER_ID = "477260225270";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
    public static final String PROPERTY_USER = "user";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_PASS = "pass";
    public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;
    public static final String TAG = "GCMDemo";
    public static final String sharePreference = "preferenciasAndroid";
}
