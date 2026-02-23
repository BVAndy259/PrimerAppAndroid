# Implementación de UsuarioManager con Persistencia de Datos

### 1. Importaciones Críticas

```java
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
```

* **`Context`**: Interfaz abstracta al entorno global de la aplicación. Otorga acceso a recursos, bases de datos y preferencias del sistema operativo.
* **`SharedPreferences`**: Mecanismo de almacenamiento persistente nativo de Android basado en archivos XML ocultos. Guarda datos primitivos estructurados en pares clave-valor.
* **`Gson`**: Biblioteca externa encargada de la serialización y deserialización. Convierte la estructura de datos Java (`HashMap`) a un formato de texto estándar (`JSON`) y viceversa.
* **`TypeToken`**: Clase de la librería Gson empleada para forzar la retención de la información del tipo genérico (`HashMap<String, String[]>`) durante el tiempo de ejecución, eludiendo el proceso de *Type Erasure* (borrado de tipos) nativo del compilador de Java.

### 2. Constantes y Variables de Estado

```java
private static final String PREFS_NAME = "UsuariosPrefs";
private static final String KEY_USUARIOS = "usuarios_map";
private static UsuarioManager instancia;
private HashMap<String, String[]> usuarios = new HashMap<>();
private final Gson gson = new Gson();
```

* **`PREFS_NAME`**: Define el nombre físico del archivo XML en el almacenamiento interno del dispositivo donde operará `SharedPreferences`.
* **`KEY_USUARIOS`**: Identificador único bajo el cual se indexará la cadena JSON completa dentro del archivo de preferencias.
* **`gson`**: Objeto inmutable instanciado de una vez para gestionar todas las transformaciones JSON de la clase.

### 3. Patrón Singleton con Sincronización

```java
public static synchronized UsuarioManager getInstance(Context context) {
    if (instancia == null) {
        instancia = new UsuarioManager();
        instancia.cargarUsuarios(context);
    }
    return instancia;
}
```

* **`synchronized`**: Modificador de control de concurrencia. Bloquea el hilo de ejecución para garantizar que múltiples hilos no evalúen la condición `instancia == null` simultáneamente. Previene la creación de múltiples instancias concurrentes (Thread-Safe).
* **Inyección en cascada**: Al crearse la instancia única, invoca de inmediato `cargarUsuarios(context)` para popular la memoria RAM con los datos persistidos en almacenamiento.

### 4. Deserialización y Carga de Datos

```java
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
```

* **`Context.MODE_PRIVATE`**: Nivel de seguridad que restringe la lectura y escritura del archivo de preferencias exclusivamente a la aplicación propietaria.
* **`getString(KEY, default)`**: Recupera el valor asociado a la clave. Retorna `null` si el archivo está vacío o la clave no existe.
* **`fromJson(String, Type)`**: Transforma la cadena JSON pura estructurándola de vuelta al `HashMap` original según las reglas establecidas por el `TypeToken`.

### 5. Serialización y Guardado de Datos

```java
private void guardarUsuarios(Context context) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();

    String json = gson.toJson(usuarios);
    editor.putString(KEY_USUARIOS, json);
    editor.apply();
}
```

* **`SharedPreferences.Editor`**: Interfaz auxiliar requerida para modificar los datos del archivo de preferencias.
* **`toJson(Object)`**: Lee la estructura actual en memoria del `HashMap` y produce un `String` plano formateado en JSON.
* **`apply()`**: Escribe las transacciones del `Editor` en la memoria en disco de manera asíncrona. A diferencia de `commit()`, no bloquea el hilo principal y es óptimo para operaciones en segundo plano.

### 6. Lógica de Registro Modificada

```java
public boolean registrar(String email, String nombre, String contrasena, Context context) {
    if (usuarios.containsKey(email)) {
        return false;
    }
    usuarios.put(email, new String[]{nombre, contrasena});
    guardarUsuarios(context);
    return true;
}
```

* **Alteración arquitectónica**: El método ahora exige una referencia al objeto `Context`.
* **Persistencia en caliente**: Inmediatamente después de insertar una nueva clave en el `HashMap` (`put`), ejecuta `guardarUsuarios(context)` para forzar un volcado de la memoria RAM al almacenamiento físico.

### 7. Lógica de Autenticación Mantenida

```java
public String login(String email, String contrasena) {
    if (usuarios.containsKey(email)) {
        String[] datos = usuarios.get(email);
        if (datos != null && datos[1].equals(contrasena)) {
            return datos[0];
        }
    }
    return null;
}
```

* **`datos != null`**: Mecanismo de seguridad adicional para prevenir excepciones `NullPointerException` en caso de corrupción de datos durante el volcado y reconstrucción por parte de Gson.

### 8. Lógica de Destrucción de Datos

```java
public void limpiarUsuarios(Context context) {
    usuarios.clear();
    guardarUsuarios(context);
}
```

* **`clear()`**: Operación nativa de la clase `Map` que elimina todas las asignaciones de clave-valor almacenadas en memoria.
* **Sincronización de estado vacío**: Llama a `guardarUsuarios` para sobrescribir el archivo JSON persistente con una estructura vacía.