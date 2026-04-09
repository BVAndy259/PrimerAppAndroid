package com.lordkratos.gestion501.customers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lordkratos.gestion501.R;
import com.lordkratos.gestion501.model.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerListActivity extends AppCompatActivity {
    private static final String TAG = "CustomerListActivity";
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private TextInputEditText etSearchCustomer;
    private final List<Customer> allCustomers = new ArrayList<>();
    private final List<Customer> filteredCustomers = new ArrayList<>();
    private ValueEventListener customersListener;
    private CustomerAdapter customerAdapter;
    private String currentSearchText = "";

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

        etSearchCustomer = findViewById(R.id.etSearchCustomer);

        RecyclerView recyclerViewCustomers = findViewById(R.id.recyclerviewCustomer);
        recyclerViewCustomers.setHasFixedSize(true);
        recyclerViewCustomers.setLayoutManager(new GridLayoutManager(CustomerListActivity.this, 2));
        customerAdapter = new CustomerAdapter();
        recyclerViewCustomers.setAdapter(customerAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "Sesion expirada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupCustomerListener();
        setupSearchCustomer();

        FloatingActionButton btnAddCustomer = findViewById(R.id.btnAddCustomer);
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

    private void setupCustomerListener() {
        customersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allCustomers.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Customer customer = parseCustomerSnapshot(child);
                    if (customer != null) {
                        allCustomers.add(customer);
                    }
                }
                applyCurrentFilter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error leyendo clientes", error.toException());
                Toast.makeText(CustomerListActivity.this, "No se pudo cargar clientes", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void setupSearchCustomer() {
        if (etSearchCustomer == null) {
            Log.e(TAG, "etSearchCustomer es null. Revisa activity_customer_list.xml");
            return;
        }
        etSearchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No-op
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCustomer(s == null ? "" : s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No-op
            }
        });
    }

    private void filterCustomer(@NonNull String text) {
        if (firebaseUser == null || customerAdapter == null) {
            return;
        }

        String normalized = text.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals(currentSearchText)) {
            return;
        }
        currentSearchText = normalized;
        applyCurrentFilter();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void applyCurrentFilter() {
        filteredCustomers.clear();
        if (currentSearchText.isEmpty()) {
            filteredCustomers.addAll(allCustomers);
        } else {
            for (Customer customer : allCustomers) {
                if (matchesCustomer(customer, currentSearchText)) {
                    filteredCustomers.add(customer);
                }
            }
        }
        customerAdapter.notifyDataSetChanged();
    }

    private boolean matchesCustomer(@NonNull Customer customer, @NonNull String search) {
        if (search.isEmpty()) {
            return true;
        }

        if (containsIgnoreCase(customer.getNames(), search)) {
            return true;
        }

        if (containsIgnoreCase(customer.getLastName(), search)) {
            return true;
        }

        String fullName = (safe(customer.getNames()) + " " + safe(customer.getLastName())).trim();
        if (containsIgnoreCase(fullName, search)) {
            return true;
        }

        String phoneDigits = digitsOnly(customer.getPhoneNumber());
        String searchDigits = digitsOnly(search);
        return !searchDigits.isEmpty() && phoneDigits.contains(searchDigits);
    }

    private boolean containsIgnoreCase(String value, @NonNull String search) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(search);
    }

    private String digitsOnly(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.replaceAll("\\D+", "");
    }

    private Customer parseCustomerSnapshot(@NonNull DataSnapshot snapshot) {
        try {
            Customer customer = new Customer();
            customer.setCustomerId(readAsString(snapshot, "customerId", snapshot.getKey()));
            customer.setCustomerUid(readAsString(snapshot, "customerUid", ""));
            customer.setNames(readAsString(snapshot, "names", ""));
            customer.setLastName(readAsString(snapshot, "lastName", ""));
            customer.setEmail(readAsString(snapshot, "email", ""));
            customer.setDni(readAsString(snapshot, "dni", ""));
            customer.setPhoneNumber(readAsString(snapshot, "phoneNumber", ""));
            customer.setDirection(readAsString(snapshot, "direction", ""));
            customer.setBase64Image(readAsString(snapshot, "base64Image", ""));
            return customer;
        } catch (RuntimeException e) {
            Log.e(TAG, "Error parseando customer: " + snapshot.getKey(), e);
            return null;
        }
    }

    private String readAsString(@NonNull DataSnapshot snapshot, @NonNull String key, @NonNull String fallback) {
        Object value = snapshot.child(key).getValue();
        if (value == null) {
            return fallback;
        }
        return String.valueOf(value);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private class CustomerAdapter extends RecyclerView.Adapter<ViewHolderCustomer> {
        @NonNull
        @Override
        public ViewHolderCustomer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
            ViewHolderCustomer viewHolderCustomer = new ViewHolderCustomer(view);
            viewHolderCustomer.setOnClickListener(new ViewHolderCustomer.clickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(CustomerListActivity.this, "on Item Click", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    Toast.makeText(CustomerListActivity.this, "on Item Long Click", Toast.LENGTH_SHORT).show();
                }
            });
            return viewHolderCustomer;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolderCustomer holder, int position) {
            Customer customer = filteredCustomers.get(position);
            holder.setCustomerData(
                    getApplicationContext(),
                    safe(customer.getCustomerUid()),
                    safe(customer.getCustomerId()),
                    safe(customer.getNames()),
                    safe(customer.getLastName()),
                    safe(customer.getEmail()),
                    safe(customer.getDni()),
                    safe(customer.getDirection()),
                    safe(customer.getPhoneNumber()),
                    safe(customer.getBase64Image())
            );
        }

        @Override
        public int getItemCount() {
            return filteredCustomers.size();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseUser != null && customersListener != null) {
            databaseReference
                    .child(firebaseUser.getUid())
                    .child("customers")
                    .addValueEventListener(customersListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseUser != null && customersListener != null) {
            databaseReference
                    .child(firebaseUser.getUid())
                    .child("customers")
                    .removeEventListener(customersListener);
        }
    }
}