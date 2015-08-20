package com.example.andres.chatandroid.chat;

import android.util.Log;

import com.example.andres.chatandroid.chat.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by andres on 04/07/2015.
 */

/**
 * esta clase es la encargada de realizar la conecciones con el servidor principal para relazar peticiones
 * de informacion o de registro en las bases de datos principales.
 */
public class SolicitudesHTTP {

    /**
     * Metodo encargado de realizar el registro de un nuevo contacto del usuario
     * @param carne
     * @return
     */
    public JSONObject Post(String carne) {

        JSONObject json = new JSONObject();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Common.getServerUrl() + "/adicion/" + carne.trim());
        try {
            Log.i("url", Common.getServerUrl() + "/adicion/" + carne);
            HttpResponse response = client.execute(httpPost);
            Log.i("Execute", "Post");
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            Log.i("Respuesta", entity.toString());
            String builder = lecturaBuilder(entity.getContent());
            json = new JSONObject(builder);
            Log.i("Json", json.toString());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    /**
     * Metodo encargado de traer todos los contactos que estan registrados en la base de datos principal de la aplicacion.
     * @param keySearch
     * @return
     */
    public JSONArray GetContactos(String keySearch) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(Common.getServerUrl() + "/pruebaConsultas/"+keySearch);
        JSONArray json = null;
        try {

            Log.i("url", Common.getServerUrl() + "/pruebaConsultas/"+keySearch);
            HttpResponse response = httpClient.execute(get);
            Log.i("Execute", "Get");
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            Log.i("Respuesta", entity.toString());
            String builder = lecturaBuilder(entity.getContent());

            if(!(builder==null) || !builder.isEmpty())
                json = new JSONArray(builder);

            Log.i("Json", json.toString());
            System.out.println("content");

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Metodo encargado de traer las carreras disponibles por la universidad registradas en las bases de datos principlaes de la aplicaicon.
     * @return
     */
    public JSONArray Get() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(Common.getServerUrl() + "/carreras");
        JSONArray json = null;
        try {

            Log.i("url", Common.getServerUrl() + "/carreras");
            HttpResponse response = httpClient.execute(get);
            Log.i("Execute", "Get");
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            Log.i("Respuesta", entity.toString());
            String builder = lecturaBuilder(entity.getContent());

            if(!(builder==null) || !builder.isEmpty())
            json = new JSONArray(builder);

            Log.i("Json", json.toString());
            System.out.println("content");

        } catch (ClientProtocolException e) {
             e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private String lecturaBuilder(InputStream content) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(content));
        System.out.println("reader");
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
            System.out.println(line);
        }
        Log.i("builder", builder.toString());
        return builder.toString();
    }

    /**
     * Metodo encargado de realizar el registro de un nuevo usuario
     * @param identificacion
     * @param carne
     * @param nombre
     * @param email
     * @param password
     * @param carrera
     * @param semestre
     * @return
     */
    public boolean registroServidor(String identificacion, String carne, String nombre, String email, String password, String carrera, String semestre) {

        boolean reg = false;

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut put = new HttpPut(Constantes.SERVER_URL + "/registro/"+ carrera);
        Log.i("url", Constantes.SERVER_URL + "/registro/" + carrera);

        try {

            JSONObject usuario = new JSONObject();
            usuario.put("identificacion",identificacion);
            usuario.put("carne",carne);
            usuario.put("nombre",nombre);
            usuario.put("identificaciongoogle",JSONObject.NULL);
            usuario.put("email", email);
            usuario.put("password", password);
            usuario.put("semestre", Integer.getInteger(semestre));
            usuario.put("perfil",JSONObject.NULL);
            usuario.put("carrerases",new JSONArray());
            usuario.put("gruposes_1", new JSONArray());
            usuario.put("materiases", new JSONArray());

            StringEntity entity = new StringEntity(usuario.toString());

            entity.setContentType("application/json;charset=UTF-8");
            entity.setContentEncoding(new
                    BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

            put.setEntity(entity);

            HttpResponse resp = httpClient.execute(put);
            System.out.println("execute");
            System.out.println(resp);

            String respStr = EntityUtils.toString(resp.getEntity());
            System.out.println(respStr);

            if(respStr.equals("true")){
                Log.d(Constantes.TAG, "Registrado en mi servidor.");
                reg = true;
                System.out.println("RegistroSatisfactorio");
            }
        } catch (Exception e) {
            Log.d(Constantes.TAG, "Error registro en mi servidor: " + e.getCause() + " || " + e.getMessage() + "||" + e.getStackTrace());
        }

        return reg;
    }

    /**
     * metodo encargad de realizar una peticion a los servidores con el fin de solicitar permiso de acceso a la aplicacion
     * @param usuario
     * @param password
     * @param regId
     * @return
     */
    public boolean inicioSesion(String usuario, String password, String regId){
        boolean respuesta = false;

        HttpClient httpClient = new DefaultHttpClient();

        HttpGet post =
                new HttpGet(Constantes.SERVER_URL + "/login/" + usuario.trim() + "/" + password.trim() + "/" + regId.trim());

        Log.i("URL",Constantes.SERVER_URL + "/login/" + usuario + "/" + password + "/" + regId);

        post.setHeader("content-type", "application/json");

        try {

            HttpResponse resp = httpClient.execute(post);
            String respStr = EntityUtils.toString(resp.getEntity());
            Log.i("Respuesta", respStr);
                if(respStr.equals("true"))
                    respuesta = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return respuesta;
    }

    /**
     * Metodo encargado de realizar un envio de un nuevo mensaje a un contacto o grupo determinado
     * @param remitente
     * @param destinatario
     * @param contenido
     * @return
     */
    public String send(String remitente, String destinatario, String contenido){
        String reg = "Message could not be sent";

        HttpClient httpClient = new DefaultHttpClient();
        HttpPut put = new HttpPut(Constantes.SERVER_URL + "/mensaje");
        Log.i("url", Constantes.SERVER_URL + "/mensaje");

        try {
            Log.i("datos", remitente + "/" + destinatario);
            JSONObject mensaje = new JSONObject();
            //usuario.put("id",0);

            if(destinatario.contains("grupo")){
                String idgrupo = destinatario.replace("grupo ", "");
                mensaje.put("grupos",jsonGrupo(idgrupo));
                mensaje.put("usuariosByUsuariodestino", JSONObject.NULL);
            }else{
                mensaje.put("grupos",JSONObject.NULL);
                mensaje.put("usuariosByUsuariodestino", JsonUsuario(destinatario));
            }

            mensaje.put("usuariosByUsuariosorigen",JsonUsuario(remitente));
            mensaje.put("fecha", new Date());
            mensaje.put("visto", JSONObject.NULL);
            mensaje.put("contenido", contenido);

            Log.i("JSIN",mensaje.toString());

            StringEntity entity = new StringEntity(mensaje.toString());

            entity.setContentType("application/json;charset=UTF-8");
            entity.setContentEncoding(new
                    BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

            put.setEntity(entity);

            HttpResponse resp = httpClient.execute(put);
            System.out.println("execute");
            System.out.println(resp);

            String respStr = EntityUtils.toString(resp.getEntity());
            System.out.println(respStr);

            if(respStr.equals("true")){
                Log.d(Constantes.TAG, "Mensaje enviado");
                reg = "";
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reg;
    }

    public JSONObject JsonUsuario(String usuario) throws JSONException {
        JSONObject usuarioDestino = new JSONObject();
        usuarioDestino.put("identificacion",usuario);
        usuarioDestino.put("carne",usuario);
        usuarioDestino.put("nombre",JSONObject.NULL);
        usuarioDestino.put("identificaciongoogle",JSONObject.NULL);
        usuarioDestino.put("email",JSONObject.NULL);
        usuarioDestino.put("password",JSONObject.NULL);
        usuarioDestino.put("perfil",JSONObject.NULL);
        usuarioDestino.put("carrerases",new JSONArray());
        usuarioDestino.put("gruposes_1", new JSONArray());
        usuarioDestino.put("semestre", JSONObject.NULL);
        usuarioDestino.put("materiases", new JSONArray());

        return usuarioDestino;

    }

    /**
     * metodo encargado de enviar la solicitud de creacion de un nuevo grupo de conversacion
     * @param nombre
     * @param Administrador
     * @param usuarios
     * @return
     */
    public boolean registroGrupo(String nombre, String Administrador, ArrayList<String> usuarios){
        boolean respuesta = false;

        HttpClient httpClient = new DefaultHttpClient();

        HttpPost post =
                new HttpPost(Constantes.SERVER_URL + "/adicion/grupo");

        Log.i("URL", Constantes.SERVER_URL + "/adicion/grupo");

        post.setHeader("content-type", "application/json");
        try {

            JSONObject grupo = new JSONObject();

            grupo.put("id", 0);
            grupo.put("usuarios", JsonUsuario(Administrador));
            grupo.put("nombre", nombre);
            grupo.put("fechacreacion", JSONObject.NULL);
            grupo.put("estado", JSONObject.NULL);
            grupo.put("tipoprivado",JSONObject.NULL);
            grupo.put("usuarioses",usuariosGrupo(usuarios));
            grupo.put("mensajeses",new JSONArray());

            Log.i("JSON", grupo.toString());

            StringEntity entity = new StringEntity(grupo.toString());

            entity.setContentType("application/json;charset=UTF-8");
            entity.setContentEncoding(new
                    BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

            post.setEntity(entity);

            HttpResponse resp = httpClient.execute(post);
            String respStr = EntityUtils.toString(resp.getEntity());
            Log.i("Respuesta", respStr);
            if(respStr.equals("true"))
                respuesta = true;
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return respuesta;
    }


    private JSONArray usuariosGrupo(ArrayList<String> usuarios) throws JSONException {
        JSONArray usuarioses = new JSONArray();

        for(String usuario : usuarios){
            usuarioses.put(JsonUsuario(usuario));
        }


        return usuarioses;
    }

    private JSONObject jsonGrupo(String idGrupo) throws JSONException {
        JSONObject grupo = new JSONObject();
        grupo.put("id", idGrupo);
        grupo.put("usuarios", JSONObject.NULL);
        grupo.put("nombre", JSONObject.NULL);
        grupo.put("fechacreacion", JSONObject.NULL);
        grupo.put("estado", JSONObject.NULL);
        grupo.put("tipoprivado",JSONObject.NULL);
        grupo.put("usuarioses",JSONObject.NULL);
        grupo.put("mensajeses",JSONObject.NULL);

        return grupo;
    }

}
