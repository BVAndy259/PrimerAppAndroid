package com.lordkratos.gestion501.misDatos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MisDatosActivity extends AppCompatActivity {
    private TextView tvCorreo, tvCodigo;
    private EditText etFechaNac, etTelefono;
    private ImageView ivCakeDate, ivPhone;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference Usuarios;
    private final Calendar calendar = Calendar.getInstance();

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
        etFechaNac = findViewById(R.id.etFechaNacMD);
        etTelefono = findViewById(R.id.etTelefonoMD);
        ivCakeDate = findViewById(R.id.ivCakeDate);
        ivPhone = findViewById(R.id.ivPhone);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Usuarios = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Usuarios");

        View.OnClickListener fechaClickListener = v -> mostrarDatePicker();
        etFechaNac.setOnClickListener(fechaClickListener);
        ivCakeDate.setOnClickListener(fechaClickListener);

        ivPhone.setOnClickListener(v -> mostrarTeclado());
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

    private void mostrarDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            calendar.set(selectedYear, selectedMonth, selectedDay);
            etFechaNac.setText(formatearFecha());
        }, year, month, day);

        dialog.show();
    }

    private String formatearFecha() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formato.format(calendar.getTime());
    }

    private void mostrarTeclado() {
        etTelefono.requestFocus();
        etTelefono.setSelection(etTelefono.getText().length());

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etTelefono, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}