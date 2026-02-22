package com.lordkratos.gestion501;

import java.util.HashMap;

public class UsuarioManager {

    // Instancia única (patrón Singleton)
    private static UsuarioManager instancia;

    // Mapa que guarda email
    private HashMap<String, String[]> usuarios = new HashMap<>();

    private UsuarioManager() {}

    public static UsuarioManager getInstance() {
        if (instancia == null) {
            instancia = new UsuarioManager();
        }
        return instancia;
    }

    // Registrar un nuevo usuario
    public boolean registrar(String email, String nombre, String contrasena) {
        if (usuarios.containsKey(email)) {
            return false;
        }
        usuarios.put(email, new String[]{nombre, contrasena});
        return true;
    }

    // Validar credenciales al hacer login
    public String login(String email, String contrasena) {
        if (usuarios.containsKey(email)) {
            String[] datos = usuarios.get(email);
            if (datos[1].equals(contrasena)) {
                return datos[0];
            }
        }
        return null;
    }
}