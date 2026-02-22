# Implementación de MainActivity: Estructura y Conceptos

### 1. Paquete e Importaciones

```java
package com.lordkratos.gestion501;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputLayout;
```

* **`EdgeToEdge`**: Utilidad para habilitar el diseño de pantalla completa, permitiendo que la interfaz se dibuje detrás de las barras del sistema (estado y navegación).
* **`WindowInsetsCompat` / `Insets**`: Clases para gestionar los márgenes seguros definidos por el sistema operativo, evitando que los elementos de la interfaz colisionen con el hardware (notch) o los controles del sistema.
* **`ViewCompat`**: Clase de compatibilidad que provee acceso a funcionalidades de vistas avanzadas en versiones anteriores de Android.

### 2. Declaración de Clase y Variables de Interfaz

```java
public class MainActivity extends AppCompatActivity {
    private TextInputLayout textInputLayout2, textInputLayout3;
    private Button button;
    private TextView tvRegistro;
```

* **Variables de instancia**: Referencias privadas a los componentes visuales definidos en `activity_main.xml`. Se recomienda una nomenclatura descriptiva (ej. `tilUsuario`, `btnIngresar`) en lugar de identificadores genéricos (`textInputLayout2`, `button`).

### 3. Ciclo de Vida y Gestión de Pantalla Completa

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_main);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });
```

* **`EdgeToEdge.enable(this)`**: Activa el renderizado de la actividad ocupando toda la pantalla física.
* **`setOnApplyWindowInsetsListener`**: Intercepta la información métrica de las barras del sistema.
* **`insets.getInsets(...)`**: Obtiene las dimensiones exactas (izquierda, superior, derecha, inferior) ocupadas por las barras del sistema operativo.
* **`v.setPadding(...)`**: Aplica un relleno dinámico al contenedor principal (`R.id.main`) equivalente al tamaño de las barras del sistema. Impide que la interfaz quede oculta o sea inaccesible.

### 4. Enlace de Vistas (View Binding)

```java
    textInputLayout2 = findViewById(R.id.textInputLayout2);
    textInputLayout3 = findViewById(R.id.textInputLayout3);
    button = findViewById(R.id.button);
    tvRegistro = findViewById(R.id.textView5);
```

* **`findViewById(int id)`**: Asigna las instancias de los objetos XML creados en memoria a las variables Java correspondientes para su manipulación programática.

### 5. Configuración de Eventos de Interacción

```java
    button.setOnClickListener(v -> validarLogin());
    tvRegistro.setOnClickListener(v -> {startActivity(new Intent(this, RegistroActivity.class));});
}
```

* **Eventos de toque**: Delegan la ejecución de lógica específica al interactuar con la interfaz. `button` dispara el proceso de autenticación, y `tvRegistro` instancia una navegación hacia `RegistroActivity` mediante un `Intent` explícito.

### 6. Extracción y Saneamiento de Datos

```java
private void validarLogin() {
    String usuario = textInputLayout2.getEditText().getText().toString().trim();
    String contrasena = textInputLayout3.getEditText().getText().toString().trim();
```

* **Procesamiento de entrada**: Extrae el valor crudo del campo de texto interior, lo convierte a tipo cadena inmutable y ejecuta `trim()` para suprimir espacios residuales en los extremos.

### 7. Control de Flujo y Autenticación

```java
    if (usuario.isEmpty() || contrasena.isEmpty()) {
        Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
        return;
    }

    String nombre = UsuarioManager.getInstance().login(usuario, contrasena);
```

* **Corte por validación**: Interrumpe la ejecución del método (`return`) si detecta campos vacíos.
* **Invocación del Singleton**: Llama al método `login` del `UsuarioManager`. Pasa las credenciales capturadas para su verificación contra el `HashMap` en memoria.

### 8. Manejo de Respuesta de Autenticación

```java
    if (nombre != null) {
        Toast.makeText(this, "¡Bienvenido, " + nombre + "!", Toast.LENGTH_LONG).show();
    } else {
        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        textInputLayout3.getEditText().setText("");
    }
}
```

* **Condicional de éxito**: Evalúa el retorno del `UsuarioManager`. Si el valor es distinto a nulo, significa que las credenciales coinciden y el nombre de usuario fue recuperado.
* **Manejo de error**: Si el retorno es nulo, indica fallo de autenticación. Muestra un aviso y limpia programáticamente (`setText("")`) el campo de la contraseña para requerir un nuevo ingreso.