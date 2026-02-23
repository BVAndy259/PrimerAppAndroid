package com.lordkratos.gestion501;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class UsuarioManager {

    private static final String PREFS_NAME = "UsuariosPrefs";
    private static final String KEY_USUARIOS = "usuarios_map";

    private static UsuarioManager instancia;
    private HashMap<String, String[]> usuarios = new HashMap<>();
    private final Gson gson = new Gson();

    private UsuarioManager() {}

    public static synchronized UsuarioManager getInstance(Context context) {
        if (instancia == null) {
            instancia = new UsuarioManager();
            instancia.cargarUsuarios(context);
        }
        return instancia;
    }

    // Carga los usuarios
    private void cargarUsuarios(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_USUARIOS, null);

        if (json != null) {
            Type type = new TypeToken<HashMap<String, String[]>>() {}.getType();
            usuarios = gson.fromJson(json, type);
            if (usuarios == null) {
                usuarios = new HashMap<>();
            }
        }
    }

    // Guarda TODOS los usuarios
    private void guardarUsuarios(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String json = gson.toJson(usuarios);
        editor.putString(KEY_USUARIOS, json);
        editor.apply();
    }

    public boolean registrar(String email, String nombre, String contrasena, Context context) {
        if (usuarios.containsKey(email)) {
            return false;
        }
        usuarios.put(email, new String[]{nombre, contrasena});
        guardarUsuarios(context);
        return true;
    }

    public String login(String email, String contrasena) {
        if (usuarios.containsKey(email)) {
            String[] datos = usuarios.get(email);
            if (datos != null && datos[1].equals(contrasena)) {
                return datos[0];
            }
        }
        return null;
    }

    // Método para limpiar
    public void limpiarUsuarios(Context context) {
        usuarios.clear();
        guardarUsuarios(context);
    }
}