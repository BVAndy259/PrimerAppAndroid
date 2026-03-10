package com.lordkratos.gestion501;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {
    private EditText etnombre, etapellido, etcorreo, etpassword, etconfirpassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private TextView lblLogin;
    private Button btnRegistrar;
    private String nombre = "", apellido = "", correo = "", password = "", confirpassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lblLogin = findViewById(R.id.lblIrLogin);

        etnombre = findViewById(R.id.etNombre);
        etapellido = findViewById(R.id.etApellido);
        etcorreo = findViewById(R.id.etCorreo);
        etpassword = findViewById(R.id.etPassword);
        etconfirpassword = findViewById(R.id.etConfirmarPassword);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Generar Instancias respectivas
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegistroActivity.this);
        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);

        lblLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(RegistroActivity.this, MainActivity.class));
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });
    }

    private void validarDatos() {
        nombre = etnombre.getText().toString().trim();
        apellido = etapellido.getText().toString().trim();
        correo = etcorreo.getText().toString().trim();
        password = etpassword.getText().toString().trim();
        confirpassword = etconfirpassword.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "El campo nombre está vacío", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(apellido)) {
            Toast.makeText(this, "El campo apellido está vacío", Toast.LENGTH_LONG).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password) || password.length() < 8) {
            Toast.makeText(this, "Ingrese una contraseña de 8 caracteres como mínimo", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confirpassword) || confirpassword.length() < 8) {
            Toast.makeText(this, "Repita la contraseña para confirmar", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirpassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            registrar();
        }
    }

    private void registrar() {
        progressDialog.setMessage("Registrando Usuario");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        guardarUsuario();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistroActivity.this, "Ocurrió un problema, revisa los campos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarUsuario() {
        progressDialog.setMessage("Guardando Información...");
        progressDialog.show();

        String uId = firebaseAuth.getUid();
        HashMap<String, String> datosusuario = new HashMap<>();
        datosusuario.put("uid", uId);
        datosusuario.put("nombre", nombre);
        datosusuario.put("apellido", apellido);
        datosusuario.put("correo", correo);
        datosusuario.put("password", password);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uId).setValue(datosusuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(RegistroActivity.this, "Usuario Creado Exitosamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegistroActivity.this, DashboardActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegistroActivity.this, "Ocurrió un problema al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}