package ues.alexus21.travelingapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.models.ListaDestinos;
import ues.alexus21.travelingapp.validations.NetworkChecker;
import ues.alexus21.travelingapp.validations.NewPostValidation;

public class NuevoPostActivity extends AppCompatActivity {

    ImageView imgAtras, imgDestino;
    EditText editTextPlaceName, editTextPlaceLocation, editTextPlaceDescription;
    Button buttonAddNewImage, btnPublish;
    ILocalUserDAO localUserDAO;
    Bitmap selectedImageBitmap = null;
    Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_post);

        imgAtras = findViewById(R.id.imgAtras);
        imgDestino = findViewById(R.id.imgDestino);
        editTextPlaceName = findViewById(R.id.editTextPlaceName);
        editTextPlaceDescription = findViewById(R.id.editTextPlaceDescription);
        editTextPlaceLocation = findViewById(R.id.editTextPlaceLocation);
        btnPublish = findViewById(R.id.btnPublish);
        buttonAddNewImage = findViewById(R.id.buttonAddNewImage);
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        buttonAddNewImage.setOnClickListener(v -> showConfirmationDialog());

        btnPublish.setOnClickListener(v -> {
            if (NetworkChecker.checkInternetConnection(this)) {
                mostrarMensaje("No hay conexión a internet");
                return;
            }

            String placeName = editTextPlaceName.getText().toString();
            String placeDescription = editTextPlaceDescription.getText().toString();
            String placeLocation = editTextPlaceLocation.getText().toString();

            if (!NewPostValidation.validateNewPost(placeName, placeDescription, placeLocation,
                    editTextPlaceName, editTextPlaceDescription, editTextPlaceLocation)) {
                return;
            }

            if (selectedImageBitmap == null && selectedImageUri == null) {
                Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            disableUploadButton();

            if (selectedImageBitmap != null) {
                uploadImageToFirebaseStorage(selectedImageBitmap);
            } else {
                uploadImageToFirebaseStorage(selectedImageUri);
            }
        });

        imgAtras.setOnClickListener(v -> {
            startActivity(new Intent(NuevoPostActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una acción");
        builder.setMessage("¿Cómo deseas continuar?");

        builder.setPositiveButton("Cámara", (dialog, which) -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        });

        builder.setNegativeButton("Galería", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2);
        });

        builder.setNeutralButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            selectedImageBitmap = (Bitmap) extras.get("data");
            imgDestino.setImageBitmap(selectedImageBitmap);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            imgDestino.setImageURI(selectedImageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imageName = UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + localUserDAO.getUserId() + "/" + imageName);
        UploadTask uploadTask = storageRef.putBytes(imageBytes);

        handleUploadTask(uploadTask, storageRef);
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String imageName = UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + localUserDAO.getUserId() + "/" + imageName);
        UploadTask uploadTask = storageRef.putFile(imageUri);

        handleUploadTask(uploadTask, storageRef);
    }

    private void handleUploadTask(UploadTask uploadTask, StorageReference storageRef) {
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                saveImageInfoToDatabase(imageUrl);
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Failed to get download URL", e);
                enableUploadButton();
            });
        }).addOnFailureListener(exception -> {
            Log.e("Firebase", "Upload failed", exception);
            enableUploadButton();
        });
    }

    private void saveImageInfoToDatabase(String imageUrl) {
        // Obtener datos de la interfaz de usuario
        String placeName = editTextPlaceName.getText().toString();
        String placeDescription = editTextPlaceDescription.getText().toString();
        String placeLocation = editTextPlaceLocation.getText().toString();
        String userId = localUserDAO.getUserId();

        // Obtener una referencia a Firebase Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String uuid = reference.push().getKey();

        // Crear un nuevo destino
        ListaDestinos destino = new ListaDestinos(
                placeDescription,
                imageUrl,
                placeLocation,
                placeName,
                userId,
                uuid
        );

        // Insertar destino en Firebase
        reference.child("destination").child(uuid).setValue(destino)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Database entry created successfully");
                    mostrarMensaje("Publicación exitosa");
                    enableUploadButton();
                    startActivity(new Intent(NuevoPostActivity.this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to create database entry", e);
                    enableUploadButton();
                });
    }


    private void disableUploadButton() {
        btnPublish.setEnabled(false);
    }

    private void enableUploadButton() {
        btnPublish.setEnabled(true);
    }
}
