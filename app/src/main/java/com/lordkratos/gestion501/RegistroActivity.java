package com.lordkratos.gestion501;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class RegistroActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilEmail, tilPassword, tilConfirmarPassword;
    private Button btnRegistrar;
    private TextView tvVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        tilNombre            = findViewById(R.id.tilNombre);
        tilEmail             = findViewById(R.id.tilEmail);
        tilPassword          = findViewById(R.id.tilPassword);
        tilConfirmarPassword = findViewById(R.id.tilConfirmarPassword);
        btnRegistrar         = findViewById(R.id.btnRegistrar);
        tvVolver             = findViewById(R.id.tvVolver);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());

        // Volver al login
        tvVolver.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void registrarUsuario() {
        String nombre    = tilNombre.getEditText().getText().toString().trim();
        String email     = tilEmail.getEditText().getText().toString().trim();
        String password  = tilPassword.getEditText().getText().toString().trim();
        String confirmar = tilConfirmarPassword.getEditText().getText().toString().trim();

        // Validar campos vacíos
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmar)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            tilConfirmarPassword.getEditText().setText("");
            return;
        }

        // Validar longitud mínima de contraseña
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // Intentar registrar
        boolean exito = UsuarioManager.getInstance().registrar(email, nombre, password);

        if (exito) {
            Toast.makeText(this, "Cuenta creada, ya puedes iniciar sesión", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show();
        }
    }
}