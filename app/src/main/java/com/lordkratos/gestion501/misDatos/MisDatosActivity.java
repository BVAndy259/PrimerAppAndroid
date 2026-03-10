package com.lordkratos.gestion501.misDatos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lordkratos.gestion501.MainActivity;
import com.lordkratos.gestion501.R;

public class MisDatosActivity extends AppCompatActivity {
    private TextView tvCorreo, tvCodigo;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference Usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_datos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvCodigo = findViewById(R.id.tvCodigoMD);
        tvCorreo = findViewById(R.id.tvCorreoMD);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Usuarios = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Usuarios");
    }

    @Override
    protected void onStart() {
        super.onStart();
        comprobarSesion();
    }

    private void comprobarSesion() {
        if (firebaseUser != null) {
            cargarDatos();
        } else {
            startActivity(new Intent(MisDatosActivity.this, MainActivity.class));
            finish();
        }
    }

    private void cargarDatos() {
        Usuarios.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uid = "" + snapshot.child("uid").getValue();
                    String correo = "" + snapshot.child("correo").getValue();
                    tvCorreo.setText(correo);
                    tvCodigo.setText(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}