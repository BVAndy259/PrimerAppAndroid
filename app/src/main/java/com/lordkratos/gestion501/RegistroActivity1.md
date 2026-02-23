# Estructura y Conceptos de RegistroActivity1

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

* **`Intent`**: Objeto de mensajería para solicitar acciones a otros componentes. Facilita la transición entre actividades.
* **`Bundle`**: Contenedor de pares clave-valor empleado para preservar el estado de la actividad a través de su ciclo de vida.
* **`AppCompatActivity`**: Clase base que provee retrocompatibilidad para características modernas de la interfaz en versiones previas de Android.
* **`TextInputLayout`**: Contenedor de Material Design que añade funcionalidades de diseño y validación a un campo de texto estándar.
* **`Toast`**: Mecanismo de notificación efímera del sistema operativo.

### 2. Declaración de Atributos

```java
public class RegistroActivity1 extends AppCompatActivity {
    private TextInputLayout tilNombre, tilEmail, tilPassword, tilConfirmarPassword;
    private Button btnRegistrar;
    private TextView tvVolver;
```

* **Variables de instancia**: Referencias privadas en memoria destinadas a almacenar los punteros hacia los elementos gráficos inflados desde el archivo XML.

### 3. Inicialización del Ciclo de Vida

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registro1);
```

* **`onCreate(Bundle)`**: Punto de entrada inicial en el ciclo de vida de la actividad. Se ejecuta una única vez por instancia.
* **`super.onCreate(...)`**: Ejecuta la lógica fundamental de inicialización de la superclase.
* **`setContentView(...)`**: Lee el archivo de diseño XML especificado, instancia los objetos visuales en memoria y los renderiza en la pantalla del dispositivo.

### 4. Enlace de la Interfaz Gráfica (View Binding)

```java
    tilNombre = findViewById(R.id.tilNombre);
    // ... mapeo del resto de variables ...
    tvVolver = findViewById(R.id.tvVolver);
```

* **`findViewById(int)`**: Atraviesa la jerarquía de vistas renderizada en pantalla para localizar el componente exacto mediante su identificador hexadecimal. Asigna esa referencia en memoria a la variable local correspondiente para permitir su manipulación programática.

### 5. Configuración de Oyentes de Eventos (Listeners)

```java
    btnRegistrar.setOnClickListener(v -> registrarUsuario());

    tvVolver.setOnClickListener(v -> {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    });
}
```

* **`setOnClickListener(...)`**: Adjunta un bloque de código al evento de interacción táctil del usuario sobre la vista respectiva.
* **`startActivity(Intent)`**: Envía una solicitud asíncrona al sistema operativo para inicializar y traer al frente a `MainActivity`.
* **`finish()`**: Desencadena la rutina de destrucción de la actividad actual, eliminándola de la pila de memoria y evitando que el usuario regrese a ella usando el botón de navegación físico.

### 6. Extracción y Limpieza de Datos

```java
private void registrarUsuario() {
    String nombre = tilNombre.getEditText().getText().toString().trim();
    String email = tilEmail.getEditText().getText().toString().trim();
    String password = tilPassword.getEditText().getText().toString().trim();
    String confirmar = tilConfirmarPassword.getEditText().getText().toString().trim();
```

* **Secuencia de extracción**: Obtiene el objeto `EditText` anidado, extrae el texto en formato `Editable`, lo transforma a una cadena inmutable `String` y ejecuta `trim()` para eliminar espacios en blanco periféricos residuales.

### 7. Cadena de Validaciones de Reglas de Negocio

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

    if (password.length() < 8) {
        Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
        return;
    }
```

* **Prevención de nulos/vacíos**: Bloquea el flujo si la longitud de cualquier cadena es cero.
* **Equivalencia estricta**: Emplea `equals()` para verificar que las secuencias de caracteres de la contraseña y su confirmación sean idénticas. En caso negativo, purga el campo de confirmación mediante `setText("")`.
* **Seguridad de longitud**: Impone un mínimo de 8 caracteres para la contraseña.
* **`return` temprano**: Termina la ejecución del método de inmediato frente a cualquier infracción de validación, abortando la comunicación con la capa de datos.

### 8. Ejecución en la Capa de Datos

```java
    boolean exito = UsuarioManager.getInstance(this).registrar(email, nombre, password, this);

    if (exito) {
        Toast.makeText(this, "Cuenta creada...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    } else {
        Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show();
    }
}
```

* **Inyección de Dependencia (Context)**: Se pasa `this` (la instancia actual de `RegistroActivity1`, que actúa como `Context`) a `getInstance(this)` y a `registrar(...)`. Esto indica que `UsuarioManager` ahora requiere el contexto de la aplicación para operar, necesario típicamente para interactuar con bases de datos persistentes como SQLite o SharedPreferences.
* **Evaluación de retorno**: Lee la bandera booleana `exito`.
* **Ruta de éxito (`true`)**: Ejecuta el cambio de pantalla hacia el inicio de sesión (`MainActivity`) y destruye la pantalla de registro (`finish()`).
* **Ruta de fallo (`false`)**: Mantiene el estado de la actividad actual y notifica la colisión de datos (correo duplicado) al usuario.