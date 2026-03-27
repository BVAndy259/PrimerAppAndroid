package com.lordkratos.gestion501.customers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lordkratos.gestion501.R;
import com.lordkratos.gestion501.model.Customer;

public class AddCustomerActivity extends AppCompatActivity {
    private TextView tvCodUserI;
    private EditText etNamesI, etLastNameI, etEmailI, etDniI, etPhoneNumberI, etDirectionI;
    private Button btnSaveI;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String uidSession = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_customer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeComponents();
        getUser();
    }

    private void initializeComponents() {
        tvCodUserI = findViewById(R.id.tvUidI);
        etNamesI = findViewById(R.id.etNombresI);
        etLastNameI = findViewById(R.id.etApellidosI);
        etEmailI = findViewById(R.id.etCorreoI);
        etDniI = findViewById(R.id.etDniI);
        etPhoneNumberI = findViewById(R.id.etTelefonoI);
        etDirectionI = findViewById(R.id.etDireccionI);
        btnSaveI = findViewById(R.id.btnGuardarI);

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();

        btnSaveI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomer();
            }
        });
    }

    private void addCustomer() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser != null ? currentUser.getUid() : uidSession;

        String names = etNamesI.getText().toString().trim();
        String lastName = etLastNameI.getText().toString().trim();
        String email = etEmailI.getText().toString().trim();
        String dni = etDniI.getText().toString().trim();
        String phoneNumber = etPhoneNumberI.getText().toString().trim();
        String direction = etDirectionI.getText().toString().trim();

        if (uid == null || uid.trim().isEmpty()) {
            Toast.makeText(this, "No hay sesion activa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (names.isEmpty()) {
            Toast.makeText(this, "Debe ingresar el nombre del customer", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference customerRef = databaseReference.child(uid).child("customers");
        String customerId = customerRef.push().getKey();
        if (customerId == null) {
            Toast.makeText(this, "No se pudo generar el ID del customer", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerUid(uid);
        customer.setNames(names);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setDni(dni);
        customer.setPhoneNumber(phoneNumber);
        customer.setDirection(direction);

        customerRef.child(customerId).setValue(customer)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Customer agregado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void getUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            uidSession =currentUser.getUid();
        } else {
            String uidExtra = getIntent().getStringExtra("uid");
            uidSession = uidExtra != null ? uidExtra.trim() : "";
        }
        tvCodUserI.setText(uidSession);
    }
}