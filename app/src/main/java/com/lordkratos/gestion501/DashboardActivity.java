package com.lordkratos.gestion501;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lordkratos.gestion501.empresa.EmpresaActivity;
import com.lordkratos.gestion501.favoritos.FavoritosActivity;
import com.lordkratos.gestion501.gastos.GastosActivity;
import com.lordkratos.gestion501.listaTareas.ListaTareasActivity;
import com.lordkratos.gestion501.misDatos.MisDatosActivity;
import com.lordkratos.gestion501.tareas.TareasActivity;

public class DashboardActivity extends AppCompatActivity {
    private Button btnCerrarSesion, btnDesarrollador;
    private CardView cvEmpresa, cvGastos, cvTareas, cvListaTareas, cvFavoritos, cvMisDatos;
    private TextView tvNombreApellido, tvCodigoU;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference Usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cvEmpresa = findViewById(R.id.cvEmpresa);
        cvGastos = findViewById(R.id.cvGastos);
        cvTareas = findViewById(R.id.cvTareas);
        cvListaTareas = findViewById(R.id.cvListaTareas);
        cvFavoritos = findViewById(R.id.cvFavoritos);
        cvMisDatos = findViewById(R.id.cvMisDatos);

        tvNombreApellido = findViewById(R.id.tvNombreApellido);
        tvCodigoU = findViewById(R.id.tvCodigoU);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnDesarrollador = findViewById(R.id.btnDesarrollador);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesión();
            }
        });

        cvEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Esto es Empresa", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, EmpresaActivity.class));
            }
        });

        cvGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Esto es Gastos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, GastosActivity.class));
            }
        });

        cvTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Esto es Tareas", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, TareasActivity.class));
            }
        });

        cvListaTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Esto es Lista de Tareas", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, ListaTareasActivity.class));
            }
        });

        cvFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Esto es Favoritos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, FavoritosActivity.class));
            }
        });

        cvMisDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DashboardActivity.this, "Esto es Misa Datos", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardActivity.this, MisDatosActivity.class));
            }
        });
    }

    private void cerrarSesión() {
        firebaseAuth.signOut();
        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onStart() {
        comprobarSesion();
        super.onStart();
    }

    private void comprobarSesion() {
        if (firebaseUser != null) {
            cargarDatos();
        } else {
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    private void cargarDatos() {
        Usuarios.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uid = "" + snapshot.child("uid").getValue();
                    String nombre = "" + snapshot.child("nombre").getValue();
                    String apellido = "" + snapshot.child("apellido").getValue();
                    tvNombreApellido.setText(nombre + " " + apellido);
                    tvCodigoU.setText(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}