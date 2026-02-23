package com.lordkratos.gestion501;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    // Atributos
    private TextInputLayout textInputLayout2, textInputLayout3;
    private Button button;
    private TextView tvRegistro;
    private int intentosFallidos = 0;
    private final int MAX_INTENTOS = 3;

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

        // Conectar vistas
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        button = findViewById(R.id.button);
        tvRegistro = findViewById(R.id.textView5);

        // Listeners
        button.setOnClickListener(v -> validarLogin());
        tvRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity1.class))
        );
    }

    // Método de validación
    private void validarLogin() {
        String usuario = textInputLayout2.getEditText().getText().toString().trim();
        String contrasena = textInputLayout3.getEditText().getText().toString().trim();

        // Validaciones básicas
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!usuario.contains("@")) {
            Toast.makeText(this, "El correo debe contener @", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intento de login
        String nombre = UsuarioManager.getInstance(this).login(usuario, contrasena);

        if (nombre != null) {
            intentosFallidos = 0;
            Toast.makeText(this, "¡Bienvenido, " + nombre + "!", Toast.LENGTH_LONG).show();

            startActivity(new Intent(this, RegistroActivity.class));
            finish();
        } else {
            // Fallo
            intentosFallidos++;
            mostrarAdvertenciaIntento();
        }
    }

    // Método de Advertencia de Intentos Fallidos
    private void mostrarAdvertenciaIntento() {
        int restantes = MAX_INTENTOS - intentosFallidos;

        if (intentosFallidos >= MAX_INTENTOS) {
            // Bloqueo
            new AlertDialog.Builder(this)
                    .setTitle("Cuenta bloqueada")
                    .setMessage("Has superado el límite de " + MAX_INTENTOS + " intentos.\nLa aplicación se cerrará.")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", (dialog, which) -> finishAffinity())
                    .show();

            button.setEnabled(false);
        } else {
            // Advertencia
            String mensaje = "Credenciales incorrectas.\n" +
                    "Intento " + intentosFallidos + " de " + MAX_INTENTOS + ".\n" +
                    "Te quedan " + restantes + " intento" + (restantes == 1 ? "" : "s") + ".";

            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        }
    }
}