package com.lordkratos.gestion501.customers;

import android.content.Intent;
import android.os.Bundle;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lordkratos.gestion501.R;
import com.lordkratos.gestion501.model.Customer;

public class CustomerListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCustomers;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseRecyclerAdapter<Customer, ViewHolderCustomer> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<Customer> firebaseRecyclerOptions;
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

        recyclerViewCustomers = findViewById(R.id.recyclerviewCustomer);
        recyclerViewCustomers.setHasFixedSize(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        customerList();

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

    private void customerList() {
        Query query = databaseReference.child(firebaseUser.getUid()).child("customers").orderByChild("names");
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Customer>().setQuery(query, Customer.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Customer, ViewHolderCustomer>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCustomer viewHolderCustomer, int i, @NonNull Customer customer) {
                viewHolderCustomer.setCustomerData(
                        getApplicationContext(),
                        customer.getCustomerUid(),
                        customer.getCustomerId(),
                        customer.getNames(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getDni(),
                        customer.getPhoneNumber(),
                        customer.getDirection()
                );
            }

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
        };

        recyclerViewCustomers.setLayoutManager(new GridLayoutManager(CustomerListActivity.this, 2));
        firebaseRecyclerAdapter.startListening();
        recyclerViewCustomers.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }
}