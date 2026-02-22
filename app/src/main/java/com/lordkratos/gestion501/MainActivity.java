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

public class MainActivity extends AppCompatActivity {

    private TextInputLayout textInputLayout2, textInputLayout3;
    private Button button;
    private TextView tvRegistro;

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

        // Conectar Vistas
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        button = findViewById(R.id.button);
        tvRegistro = findViewById(R.id.textView5);

        // Listener del botón
        button.setOnClickListener(v -> validarLogin());
        tvRegistro.setOnClickListener(v -> {startActivity(new Intent(this, RegistroActivity.class));});
    }

    private void validarLogin() {
        String usuario = textInputLayout2.getEditText().getText().toString().trim();
        String contrasena = textInputLayout3.getEditText().getText().toString().trim();

        // Validar Campos
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = UsuarioManager.getInstance().login(usuario, contrasena);

        // Credenciales válidas
        if (nombre != null) {
            Toast.makeText(this, "¡Bienvenido, " + nombre + "!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            textInputLayout3.getEditText().setText("");
        }
    }
}