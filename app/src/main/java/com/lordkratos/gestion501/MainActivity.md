# Implementación de MainActivity con Límite de Intentos: Estructura y Conceptos

### 1. Importaciones Adicionales Clave

```java
import androidx.appcompat.app.AlertDialog;
```

* **`AlertDialog`**: Clase del marco de trabajo de Android utilizada para renderizar cuadros de diálogo modales superpuestos a la interfaz principal. Interrumpe el flujo del usuario para forzar una decisión o comunicar información crítica.

### 2. Variables de Estado y Constantes de Lógica

```java
private int intentosFallidos = 0;
private final int MAX_INTENTOS = 3;
```

* **`intentosFallidos`**: Variable entera mutable que actúa como contador de estado temporal en memoria. Rastrea la cantidad de autenticaciones erróneas consecutivas.
* **`final`**: Modificador de acceso que define una constante. El valor de `MAX_INTENTOS` (3) es inmutable durante el tiempo de ejecución. Previene modificaciones accidentales de las reglas de negocio.

### 3. Configuración Inicial y Enlace (onCreate)

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    // ... configuración edge-to-edge y padding de insets ...
    textInputLayout2 = findViewById(R.id.textInputLayout2);
    // ... enlaces ...
    button.setOnClickListener(v -> validarLogin());
    tvRegistro.setOnClickListener(v -> startActivity(new Intent(this, RegistroActivity1.class)));
}
```

* **Estructura base**: Ejecuta la configuración de pantalla completa, mapea los elementos de la interfaz a las variables de instancia y asocia las funciones lambda a los eventos de clic. Dirige la acción de registro hacia `RegistroActivity1`.

### 4. Extracción y Reglas de Validación de Entrada (validarLogin)

```java
String usuario = textInputLayout2.getEditText().getText().toString().trim();
String contrasena = textInputLayout3.getEditText().getText().toString().trim();

if (usuario.isEmpty() || contrasena.isEmpty()) { return; }
if (!usuario.contains("@")) { return; }
if (contrasena.length() < 8) { return; }
```

* **Filtros de seguridad escalonados**: Evalúa secuencialmente la integridad de los datos antes de procesar la solicitud.
* **`contains("@")`**: Validación sintáctica básica de formato de correo electrónico.
* **`length() < 8`**: Validación de longitud mínima para la contraseña, aplicando políticas de seguridad en el lado del cliente para rechazar entradas no válidas prematuramente.
* **`return`**: Detiene el flujo del método de forma anticipada si alguna condición de formato no se cumple, economizando procesamiento.

### 5. Lógica de Autenticación y Reset de Estado

```java
String nombre = UsuarioManager.getInstance(this).login(usuario, contrasena);

if (nombre != null) {
    intentosFallidos = 0;
    // ... notificación y navegación ...
    finish();
} else {
    intentosFallidos++;
    mostrarAdvertenciaIntento();
}
```

* **`UsuarioManager.getInstance(this)`**: Llama a la capa de persistencia/datos pasando el contexto actual (`this`).
* **Condición de éxito**: Si el login es válido (`nombre != null`), se reinicia el contador `intentosFallidos = 0` para purgar el historial de errores. Se ejecuta la navegación y se destruye la actividad actual con `finish()`.
* **Condición de fallo**: Incrementa el contador mediante el operador unario `++` y delega la gestión del error al método auxiliar `mostrarAdvertenciaIntento()`.

### 6. Sistema de Advertencia y Bloqueo (mostrarAdvertenciaIntento)

```java
private void mostrarAdvertenciaIntento() {
    int restantes = MAX_INTENTOS - intentosFallidos;

    if (intentosFallidos >= MAX_INTENTOS) {
        new AlertDialog.Builder(this)
                .setTitle("Cuenta bloqueada")
                .setMessage("Has superado el límite de " + MAX_INTENTOS + " intentos.\nLa aplicación se cerrará.")
                .setCancelable(false)
                .setPositiveButton("Aceptar", (dialog, which) -> finishAffinity())
                .show();
        button.setEnabled(false);
    } else {
        // ... muestra Toast con intentos restantes ...
    }
}
```

* **Aritmética de control**: Calcula iterativamente los intentos disponibles (`restantes`).
* **Bifurcación de estado límite (`>= MAX_INTENTOS`)**: Activa el protocolo de seguridad máxima al agotar los intentos.
* **Patrón Builder (`AlertDialog.Builder`)**: Diseña el cuadro de diálogo de forma secuencial.
* **`setCancelable(false)`**: Bloquea la capacidad del usuario de descartar el cuadro de diálogo tocando fuera de él o usando el botón físico de retroceso. Obliga a interactuar con el botón definido.
* **`finishAffinity()`**: Método de `Activity` que no solo cierra la pantalla actual, sino que destruye todas las actividades subordinadas en la pila de tareas que compartan la misma afinidad (cierra la aplicación de raíz).
* **`button.setEnabled(false)`**: Deshabilita el componente visual interactivo del botón subyacente a nivel de interfaz para evitar entradas adicionales mientras el sistema se cierra.