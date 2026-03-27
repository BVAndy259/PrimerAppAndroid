package com.lordkratos.gestion501.customers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lordkratos.gestion501.R;

public class CustomerListActivity extends AppCompatActivity {
    private FloatingActionButton btnAddCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAddCustomer = findViewById(R.id.btnAddCustomer);
        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomerListActivity.this, "Bienvenidos a Lista de Clientes", Toast.LENGTH_SHORT).show();


                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(CustomerListActivity.this, "Sesion expirada", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(CustomerListActivity.this, AddCustomerActivity.class);
                intent.putExtra("uid", currentUser.getUid());
                startActivity(intent);
            }
        });
    }
}