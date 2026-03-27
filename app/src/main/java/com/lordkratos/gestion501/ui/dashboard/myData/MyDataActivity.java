package com.lordkratos.gestion501.ui.dashboard.myData;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Build;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lordkratos.gestion501.ui.dashboard.DashboardActivity;
import com.lordkratos.gestion501.ui.main.MainActivity;
import com.lordkratos.gestion501.R;

import java.text.SimpleDateFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyDataActivity extends AppCompatActivity {
    private TextView tvCorreo, tvCodigo;
    private EditText etNombre, etApellido, etFechaNac, etEdad, etTelefono, etDomicilio, etTiktok, etProfesion;
    private ImageView ivCakeDate, ivPhone, ivFotoPerfilMD;
    private Button btnRegistrar;
    private String nombre = "", apellido = "", fechaNac = "", edad = "", telefono = "", domicilio = "", tiktok = "", profesion = "";
    private String fotoBase64 = "";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference Usuarios;
    private ProgressDialog progressDialog;
    private Calendar calendar;
    private ActivityResultLauncher<String> seleccionarImagenLauncher;
    private ActivityResultLauncher<Void> tomarFotoLauncher;
    private boolean fotoPendienteDeGuardar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarComponentes();
        configurarSelectorImagen();
    }

    private void inicializarComponentes() {
        tvCodigo = findViewById(R.id.tvCodigoMD);
        tvCorreo = findViewById(R.id.tvCorreoMD);
        etNombre = findViewById(R.id.etNombreMD);
        etApellido = findViewById(R.id.etApellidoMD);
        etFechaNac = findViewById(R.id.etFechaNacMD);
        etEdad = findViewById(R.id.etEdadMD);
        etTelefono = findViewById(R.id.etTelefonoMD);
        etDomicilio = findViewById(R.id.etDomicilioMD);
        etTiktok = findViewById(R.id.etTiktokMD);
        etProfesion = findViewById(R.id.etProfesionMD);
        ivFotoPerfilMD = findViewById(R.id.ivFotoPerfilMD);
        ivCakeDate = findViewById(R.id.ivCakeDate);
        ivPhone = findViewById(R.id.ivPhone);
        btnRegistrar = findViewById(R.id.btnGuardarMD);

        calendar = Calendar.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        etFechaNac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });
        ivCakeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarTeclado();
            }
        });

        ivFotoPerfilMD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarOpcionesFoto();
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }

    private void configurarSelectorImagen() {
        seleccionarImagenLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) {
                return;
            }

            try {
                try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                    Bitmap bitmapOriginal = BitmapFactory.decodeStream(inputStream);
                    if (bitmapOriginal == null) {
                        Toast.makeText(this, "No se pudo leer la imagen", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    procesarBitmapSeleccionado(bitmapOriginal);
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
            }
        });

        tomarFotoLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if (bitmap == null) {
                Toast.makeText(this, "No se pudo capturar la foto", Toast.LENGTH_SHORT).show();
                return;
            }

            procesarBitmapSeleccionado(bitmap);
        });
    }

    private void mostrarOpcionesFoto() {
        String[] opciones = {"Galeria", "Camara"};
        new AlertDialog.Builder(this)
                .setTitle("Selecciona una opcion")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        seleccionarImagenLauncher.launch("image/*");
                    } else {
                        tomarFotoLauncher.launch(null);
                    }
                })
                .show();
    }

    private void procesarBitmapSeleccionado(Bitmap bitmapOriginal) {
        Bitmap bitmapReducido = Bitmap.createScaledBitmap(bitmapOriginal, 320, 320, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int calidad = 80;
        comprimirBitmap(bitmapReducido, calidad, outputStream);
        while (outputStream.size() > 250 * 1024 && calidad > 30) {
            outputStream.reset();
            calidad -= 10;
            comprimirBitmap(bitmapReducido, calidad, outputStream);
        }

        byte[] bytesImagen = outputStream.toByteArray();
        fotoBase64 = Base64.encodeToString(bytesImagen, Base64.NO_WRAP);
        fotoPendienteDeGuardar = true;
        ivFotoPerfilMD.setImageBitmap(bitmapReducido);
        Toast.makeText(this, "Foto lista. Presiona Guardar para actualizar", Toast.LENGTH_SHORT).show();
    }

    private void comprimirBitmap(Bitmap bitmap, int calidad, ByteArrayOutputStream outputStream) {
        boolean comprimio;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            comprimio = bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, calidad, outputStream);
        } else {
            // Compatibilidad para APIs antiguas donde WEBP_LOSSY no existe.
            comprimio = bitmap.compress(Bitmap.CompressFormat.WEBP, calidad, outputStream);
        }

        if (!comprimio) {
            outputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, outputStream);
        }
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
            startActivity(new Intent(MyDataActivity.this, MainActivity.class));
            finish();
        }
    }

    private void cargarDatos() {
        String uid = firebaseAuth.getUid();
        if (uid == null || uid.isEmpty()) {
            Toast.makeText(this, "No se pudo cargar el usuario actual", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuarios.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String uid = "" + snapshot.child("uid").getValue();
                    String correo = "" + snapshot.child("correo").getValue();
                    String nombre = "" + snapshot.child("nombre").getValue();
                    String apellido = "" + snapshot.child("apellido").getValue();
                    String fechaNac = "" + snapshot.child("fechaNac").getValue();
                    String edad = "" + snapshot.child("edad").getValue();
                    String telefono = "" + snapshot.child("telefono").getValue();
                    String domicilio = "" + snapshot.child("domicilio").getValue();
                    String tiktok = "" + snapshot.child("tiktok").getValue();
                    String profesion = "" + snapshot.child("profesion").getValue();
                    String fotoDb = snapshot.child("fotoBase64").getValue(String.class);

                    tvCorreo.setText(correo);
                    tvCodigo.setText(uid);
                    etNombre.setText(nombre);
                    etApellido.setText(apellido);
                    etFechaNac.setText(fechaNac);
                    etEdad.setText(edad);
                    etTelefono.setText(telefono);
                    etDomicilio.setText(domicilio);
                    etTiktok.setText(tiktok);
                    etProfesion.setText(profesion);

                    if (!fotoPendienteDeGuardar) {
                        fotoBase64 = fotoDb == null ? "" : fotoDb;
                        if (!fotoBase64.isEmpty()) {
                            try {
                                byte[] imageBytes = Base64.decode(fotoBase64, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                if (bitmap != null) {
                                    ivFotoPerfilMD.setImageBitmap(bitmap);
                                }
                            } catch (IllegalArgumentException ignored) {
                                ivFotoPerfilMD.setImageResource(R.drawable.mc_md);
                            }
                        } else {
                            ivFotoPerfilMD.setImageResource(R.drawable.mc_md);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyDataActivity.this, "Error al cargar Datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
            actualizarEdadDesdeFecha();
        }, year, month, day);

        dialog.show();
    }

    private String formatearFecha() {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formato.format(calendar.getTime());
    }

    private void actualizarEdadDesdeFecha() {
        Calendar hoy = Calendar.getInstance();

        if (calendar.after(hoy)) {
            etEdad.setText("");
            Toast.makeText(this, "La fecha de nacimiento no puede ser futura", Toast.LENGTH_SHORT).show();
            return;
        }

        int edadCalculada = hoy.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        int mesHoy = hoy.get(Calendar.MONTH);
        int diaHoy = hoy.get(Calendar.DAY_OF_MONTH);
        int mesNac = calendar.get(Calendar.MONTH);
        int diaNac = calendar.get(Calendar.DAY_OF_MONTH);

        if (mesHoy < mesNac || (mesHoy == mesNac && diaHoy < diaNac)) {
            edadCalculada--;
        }

        etEdad.setText(String.valueOf(Math.max(edadCalculada, 0)));
    }

    private void mostrarTeclado() {
        etTelefono.requestFocus();
        etTelefono.setSelection(etTelefono.getText().length());

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(etTelefono, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void validarDatos() {
        nombre = etNombre.getText().toString().trim();
        apellido = etApellido.getText().toString().trim();
        fechaNac = etFechaNac.getText().toString().trim();
        edad = etEdad.getText().toString().trim();
        telefono =etTelefono.getText().toString().trim();
        domicilio = etDomicilio.getText().toString().trim();
        tiktok = etTiktok.getText().toString().trim();
        profesion = etProfesion.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El campo nombre está vacío", Toast.LENGTH_SHORT).show();
        } else if (apellido.isEmpty()) {
            Toast.makeText(this, "El campo apellido está vacío", Toast.LENGTH_SHORT).show();
        } else if (fechaNac.isEmpty()) {
            Toast.makeText(this, "El campo fecha de nacimiento está vacío", Toast.LENGTH_SHORT).show();
        } else if (edad.isEmpty()) {
            Toast.makeText(this, "El campo edad está vacío", Toast.LENGTH_SHORT).show();
        } else if (telefono.isEmpty()) {
            Toast.makeText(this, "El campo teléfono está vacío", Toast.LENGTH_SHORT).show();
        } else if (domicilio.isEmpty()) {
            Toast.makeText(this, "El campo domicilio está vacío", Toast.LENGTH_SHORT).show();
        } else if (tiktok.isEmpty()) {
            Toast.makeText(this, "El campo TikTok está vacío", Toast.LENGTH_SHORT).show();
        } else if (profesion.isEmpty()) {
            Toast.makeText(this, "El campo profesión está vacío", Toast.LENGTH_SHORT).show();
        } else {
            procesarDatos();
        }
    }

    private void procesarDatos() {
        progressDialog.setMessage("Procesando Información...");
        progressDialog.show();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MyDataActivity.this, MainActivity.class));
            finish();
            return;
        }

        guardarDatos(user.getUid());
    }

    private void guardarDatos(String uid) {
        progressDialog.setMessage("Guardando Información...");
        progressDialog.show();

        if (uid == null || uid.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "No se pudo obtener el UID del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("uid", uid);
        datosUsuario.put("correo", firebaseUser != null && firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "");
        datosUsuario.put("nombre", nombre);
        datosUsuario.put("apellido", apellido);
        datosUsuario.put("fechaNac", fechaNac);
        datosUsuario.put("edad", edad);
        datosUsuario.put("telefono", telefono);
        datosUsuario.put("domicilio", domicilio);
        datosUsuario.put("tiktok", tiktok);
        datosUsuario.put("profesion", profesion);
        datosUsuario.put("fotoBase64", fotoBase64);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");
        databaseReference.child(uid).updateChildren(datosUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fotoPendienteDeGuardar = false;
                progressDialog.dismiss();
                Toast.makeText(MyDataActivity.this, "Datos Registrados Exitosamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MyDataActivity.this, DashboardActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MyDataActivity.this, "Error al Guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}