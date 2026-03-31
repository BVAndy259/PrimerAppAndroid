package com.lordkratos.gestion501.customers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lordkratos.gestion501.R;
import com.lordkratos.gestion501.model.Customer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddCustomerActivity extends AppCompatActivity {
    private TextView tvCodUserI;
    private EditText etNamesI, etLastNameI, etEmailI, etDniI, etPhoneNumberI, etDirectionI;
    private Button btnSaveI, btnSelectImageI, btnTakePhotoI;
    private ImageView ivCustomerPhotoI;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String uidSession = "";
    private String base64Image = "";
    private final ExecutorService imageExecutor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImageSelected);

    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), this::onCameraImageSelected);

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
        ivCustomerPhotoI = findViewById(R.id.ivCustomerPhotoI);
        btnSelectImageI = findViewById(R.id.btnSelectImageI);
        btnTakePhotoI = findViewById(R.id.btnTakePhotoI);
        etNamesI = findViewById(R.id.etNamesI);
        etLastNameI = findViewById(R.id.etLastNameI);
        etEmailI = findViewById(R.id.etEmailI);
        etDniI = findViewById(R.id.etDniI);
        etPhoneNumberI = findViewById(R.id.etPhoneNumberI);
        etDirectionI = findViewById(R.id.etAddressI);
        btnSaveI = findViewById(R.id.btnSaveI);

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();

        btnSelectImageI.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnTakePhotoI.setOnClickListener(v -> takePictureLauncher.launch(null));
        ivCustomerPhotoI.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnSaveI.setOnClickListener(v -> addCustomer());
    }

    private void onImageSelected(Uri uri) {
        if (uri == null) {
            return;
        }

        Toast.makeText(this, R.string.image_processing, Toast.LENGTH_SHORT).show();
        imageExecutor.execute(() -> {
            Bitmap decodedBitmap = decodeSampledBitmapFromUri(uri, 1280, 1280);
            if (decodedBitmap == null) {
                runOnUiThread(() -> Toast.makeText(this, R.string.image_process_error, Toast.LENGTH_SHORT).show());
                return;
            }

            Bitmap uploadBitmap = resizeBitmap(decodedBitmap, 640);
            String encoded = encodeBitmapToWebpBase64(uploadBitmap);
            if (encoded.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, R.string.image_process_error, Toast.LENGTH_SHORT).show());
                return;
            }

            Bitmap previewBitmap = resizeBitmap(decodedBitmap, 220);
            runOnUiThread(() -> {
                base64Image = encoded;
                ivCustomerPhotoI.setImageBitmap(previewBitmap);
                Toast.makeText(this, R.string.image_selected, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void onCameraImageSelected(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        Toast.makeText(this, R.string.image_processing, Toast.LENGTH_SHORT).show();
        imageExecutor.execute(() -> {
            Bitmap uploadBitmap = resizeBitmap(bitmap, 640);
            String encoded = encodeBitmapToWebpBase64(uploadBitmap);
            if (encoded.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, R.string.image_process_error, Toast.LENGTH_SHORT).show());
                return;
            }

            Bitmap previewBitmap = resizeBitmap(bitmap, 220);
            runOnUiThread(() -> {
                base64Image = encoded;
                ivCustomerPhotoI.setImageBitmap(previewBitmap);
                Toast.makeText(this, R.string.image_selected, Toast.LENGTH_SHORT).show();
            });
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
        customer.setBase64Image(base64Image);

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

    private String encodeBitmapToWebpBase64(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Bitmap.CompressFormat format = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                ? Bitmap.CompressFormat.WEBP_LOSSY
                : Bitmap.CompressFormat.WEBP;

        boolean compressed = bitmap.compress(format, 75, outputStream);
        if (!compressed) {
            return "";
        }

        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private Bitmap decodeSampledBitmapFromUri(@NonNull Uri imageUri, int reqWidth, int reqHeight) {
        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
        boundsOptions.inJustDecodeBounds = true;

        try (InputStream boundsStream = getContentResolver().openInputStream(imageUri)) {
            if (boundsStream == null) {
                return null;
            }
            BitmapFactory.decodeStream(boundsStream, null, boundsOptions);
        } catch (IOException e) {
            return null;
        }

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inSampleSize = calculateInSampleSize(boundsOptions, reqWidth, reqHeight);

        try (InputStream decodeStream = getContentResolver().openInputStream(imageUri)) {
            if (decodeStream == null) {
                return null;
            }
            return BitmapFactory.decodeStream(decodeStream, null, decodeOptions);
        } catch (IOException e) {
            return null;
        }
    }

    private int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private Bitmap resizeBitmap(@NonNull Bitmap source, int maxSide) {
        int width = source.getWidth();
        int height = source.getHeight();
        int biggestSide = Math.max(width, height);

        if (biggestSide <= maxSide) {
            return source;
        }

        float ratio = (float) maxSide / biggestSide;
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageExecutor.shutdown();
    }
}