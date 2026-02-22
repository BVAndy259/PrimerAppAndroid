# Implementación de RegistroActivity: Estructura y Conceptos

### 1. Paquete e Importaciones

```java
package com.lordkratos.gestion501;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
```

* **`Intent`**: Objeto de mensajería utilizado para solicitar una acción de otro componente de la aplicación, como la navegación entre pantallas.
* **`Bundle`**: Estructura de datos que mapea claves de cadena a varios valores, utilizada para pasar datos entre componentes o guardar y restaurar el estado de la actividad.
* **`AppCompatActivity`**: Clase base para actividades que garantiza la compatibilidad de las características modernas de la interfaz de usuario en versiones antiguas de Android.
* **`TextInputLayout`**: Componente de Material Design que envuelve un `EditText` para proporcionar etiquetas flotantes, gestión de errores y formato visual avanzado.
* **`Toast`**: Clase que provee retroalimentación visual rápida a través de pequeños mensajes emergentes temporales.

### 2. Declaración de Clase y Variables de Interfaz

```java
public class RegistroActivity extends AppCompatActivity {
    private TextInputLayout tilNombre, tilEmail, tilPassword, tilConfirmarPassword;
    private Button btnRegistrar;
    private TextView tvVolver;
```

* **`extends AppCompatActivity`**: Hereda el comportamiento estructural y el ciclo de vida estándar de una pantalla en Android.
* **Variables de instancia**: Referencias privadas a los elementos visuales que se definirán posteriormente en el archivo XML de diseño (`activity_registro.xml`).

### 3. Inicialización en el Ciclo de Vida

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registro);
    tilNombre = findViewById(R.id.tilNombre);
    // ... asignaciones subsecuentes ...
}
```

* **`@Override`**: Anotación que indica la sobrescritura explícita de un método de la clase padre.
* **`onCreate`**: Método fundamental del ciclo de vida de Android, invocado por el sistema operativo al instanciar la actividad.
* **`setContentView(int layoutResID)`**: Infla y procesa la interfaz de usuario XML para renderizarla en la pantalla.
* **`findViewById(int id)`**: Recorre el árbol de vistas inflado para localizar el componente con el ID especificado y enlaza la vista XML con el objeto Java correspondiente en memoria.

### 4. Configuración de Eventos (Listeners)

```java
btnRegistrar.setOnClickListener(v -> registrarUsuario());
tvVolver.setOnClickListener(v -> {
    startActivity(new Intent(this, MainActivity.class));
    finish();
});
```

* **`setOnClickListener`**: Asigna un bloque de código ejecutable que el sistema disparará al detectar un evento de toque del usuario sobre la vista.
* **`v ->` (Expresión Lambda)**: Sintaxis concisa introducida en Java 8 para implementar implementaciones anónimas de interfaces funcionales, en este caso, `View.OnClickListener`.
* **`startActivity(Intent)`**: Pasa el `Intent` al sistema operativo para que lance una nueva instancia de la actividad destino (`MainActivity`).
* **`finish()`**: Ordena la destrucción de la actividad actual (`RegistroActivity`), eliminándola de la pila de retroceso (back stack) y liberando sus recursos.

### 5. Extracción y Saneamiento de Datos

```java
private void registrarUsuario() {
    String nombre = tilNombre.getEditText().getText().toString().trim();
    // ... repetición para los otros campos ...
}
```

* **`getEditText()`**: Recupera la referencia del componente de entrada de texto interno contenido dentro del contenedor `TextInputLayout`.
* **`getText().toString()`**: Extrae la secuencia de caracteres editable (tipo `Editable`) ingresada por el usuario y la convierte a un objeto inmutable `String`.
* **`trim()`**: Elimina los caracteres de espacio en blanco iniciales y finales para normalizar la cadena y evitar registros vacíos o mal formateados.

### 6. Control de Flujo y Validaciones

```java
if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
    Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
    return;
}
if (!password.equals(confirmar)) {
    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
    tilConfirmarPassword.getEditText().setText("");
    return;
}
```

* **`isEmpty()`**: Verifica si la longitud de la cadena es cero.
* **`Toast.makeText(...).show()`**: Construye y encola el mensaje emergente en la interfaz de usuario.
* **`return`**: Instrucción de control que fuerza la salida inmediata y prematura del método `registrarUsuario`. Opera como un mecanismo de guardia para detener la ejecución de la lógica subsecuente si los datos no cumplen con los criterios de validación.
* **`setText("")`**: Vacía el campo de texto de confirmación de contraseña, obligando al usuario a reescribirla.

### 7. Integración de Lógica de Negocio

```java
boolean exito = UsuarioManager.getInstance().registrar(email, nombre, password);

if (exito) {
    Toast.makeText(this, "Cuenta creada...", Toast.LENGTH_LONG).show();
    startActivity(new Intent(this, MainActivity.class));
    finish();
} else {
    Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show();
}
```

* **`UsuarioManager.getInstance().registrar(...)`**: Invocación al gestor Singleton definido previamente para insertar los datos en el mapa en memoria.
* **Lógica condicional**: Evalúa la respuesta booleana `exito`. Si es `true`, notifica el éxito de la inserción de la clave en el `HashMap` y ejecuta la navegación a la pantalla principal. Si es `false`, denota una colisión de clave en el `HashMap`, notifica el error y mantiene al usuario en la pantalla actual.