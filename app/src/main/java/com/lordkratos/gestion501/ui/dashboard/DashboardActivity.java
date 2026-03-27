package com.lordkratos.gestion501.ui.dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.lordkratos.gestion501.R;
import com.lordkratos.gestion501.clientes.CustomerListActivity;
import com.lordkratos.gestion501.ui.dashboard.favoritos.FavoritosActivity;
import com.lordkratos.gestion501.ui.dashboard.gastos.GastosActivity;
import com.lordkratos.gestion501.ui.dashboard.listaTareas.ListaTareasActivity;
import com.lordkratos.gestion501.ui.dashboard.misDatos.MisDatosActivity;
import com.lordkratos.gestion501.ui.dashboard.tareas.TareasActivity;
import com.lordkratos.gestion501.ui.main.MainActivity;

public class DashboardActivity extends AppCompatActivity {
    private Button btnCerrarSesion, btnDesarrollador;
    private CardView cvClient, cvGastos, cvTareas, cvListaTareas, cvFavoritos, cvMisDatos;
    private Dialog dialogDev;
    private TextView tvNombreApellido, tvCodigoU;
    private ImageView ivFotoDashboard;
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

        cvClient = findViewById(R.id.cvClient);
        cvGastos = findViewById(R.id.cvGastos);
        cvTareas = findViewById(R.id.cvTareas);
        cvListaTareas = findViewById(R.id.cvListaTareas);
        cvFavoritos = findViewById(R.id.cvFavoritos);
        cvMisDatos = findViewById(R.id.cvMisDatos);

        tvNombreApellido = findViewById(R.id.tvNombreApellido);
        tvCodigoU = findViewById(R.id.tvCodigoU);
        ivFotoDashboard = findViewById(R.id.ivFotoDashboard);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnDesarrollador = findViewById(R.id.btnDesarrollador);
        dialogDev = new Dialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesión();
            }
        });

        btnDesarrollador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desarrollador();
            }
        });

        cvClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(DashboardActivity.this, "Sesion expirada, vuelve a iniciar sesion", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                    finish();
                    return;
                }

                Intent intent = new Intent(DashboardActivity.this, CustomerListActivity.class);
                intent.putExtra("uid", currentUser.getUid());
                startActivity(intent);
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
                Toast.makeText(DashboardActivity.this, "Esto es Mis Datos", Toast.LENGTH_SHORT).show();
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

    private void desarrollador() {
        TextView tvTelefono;
        Button volver;
        ImageButton github, youtube;

        dialogDev.setContentView(R.layout.dialog_developer);
        tvTelefono = dialogDev.findViewById(R.id.tvTelefono);
        volver = dialogDev.findViewById(R.id.btnVolverDesarrollador);
        github = dialogDev.findViewById(R.id.githubIcon);
        youtube = dialogDev.findViewById(R.id.youtubeIcon);

        tvTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = "+51984373303";
                Uri uri = Uri.parse("tel:" + numero);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String githubUrl = "https://github.com/BVAndy259";
                Uri uri = Uri.parse(githubUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ytUrl = "https://www.youtube.com/@destructor_777";
                Uri uri = Uri.parse(ytUrl);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDev.dismiss();
            }
        });
        dialogDev.show();
        dialogDev.setCanceledOnTouchOutside(false);
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
        String uidActual = firebaseAuth.getUid();
        if (uidActual == null || uidActual.isEmpty()) {
            return;
        }

        Usuarios.child(uidActual).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uid = "" + snapshot.child("uid").getValue();
                    String nombre = "" + snapshot.child("nombre").getValue();
                    String apellido = "" + snapshot.child("apellido").getValue();
                    String fotoBase64 = snapshot.child("fotoBase64").getValue(String.class);
                    tvNombreApellido.setText(nombre + " " + apellido);
                    tvCodigoU.setText(uid);

                    if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                        try {
                            byte[] imageBytes = Base64.decode(fotoBase64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            if (bitmap != null) {
                                ivFotoDashboard.setImageBitmap(bitmap);
                            } else {
                                ivFotoDashboard.setImageResource(R.drawable.bienv_1);
                            }
                        } catch (IllegalArgumentException e) {
                            ivFotoDashboard.setImageResource(R.drawable.bienv_1);
                        }
                    } else {
                        ivFotoDashboard.setImageResource(R.drawable.bienv_1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}