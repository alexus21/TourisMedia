package ues.alexus21.travelingapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.adapters.ListaDestinosAdapter;
import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.localstorage.LocalUserModel;
import ues.alexus21.travelingapp.models.ListaDestinos;
import ues.alexus21.travelingapp.validations.NetworkChecker;

public class ListaDestinosActivity extends AppCompatActivity {
    private ILocalUserDAO localUserDAO;
    private ListView ltsDestinosTuristicos;
    private ListaDestinosAdapter adapter;
    private ArrayList<ListaDestinos> listaDestinos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_destinos);

        cheackAndUploadImages(this);

        // Obtener referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        ltsDestinosTuristicos = findViewById(R.id.ltsDestinosTuristicos);
        listaDestinos = new ArrayList<>();

        if(NetworkChecker.checkInternetConnection(this)) {
            mostrarMensaje("No hay conexión a internet");
        }

        DatabaseReference destinationReference = FirebaseDatabase.getInstance().getReference("destination");
        destinationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaDestinos.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListaDestinos destino = dataSnapshot.getValue(ListaDestinos.class);
                    listaDestinos.add(destino);
                }
                adapter = new ListaDestinosAdapter(listaDestinos, ListaDestinosActivity.this);
                ltsDestinosTuristicos.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListaDestinosActivity.this, "Error al cargar los destinos turísticos", Toast.LENGTH_SHORT).show();
            }
        });

        String userId = localUserDAO.getUserId();

        // Imprimir todo lo de localUserDAO.getUser
        for (LocalUserModel user : localUserDAO.getAll()) {
            System.out.println("ID: " + user.getId());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Remote ID: " + user.getUser_remote_id());
            System.out.println("Estado: " + user.getIsLogged());
        }

    }

    private void uploadToFirebaseStorage(File file, String remoteId) {
        Uri fileUri = Uri.fromFile(file);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(fileUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            mostrarMensaje("Upload successful");
        }).addOnFailureListener(exception -> {
            mostrarMensaje("Upload failed");
        });
    }

    private void uploadToFirebaseStorage(File file) {
        Uri fileUri = Uri.fromFile(file);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(fileUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Obtener la URL de descarga de la imagen subida
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Crear un nuevo registro en Firebase Realtime Database
                String imageUrl = uri.toString();
                /*String destinationId = UUID.randomUUID().toString();*/

                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("destination");
                String destinationId = databaseRef.push().getKey();

                ListaDestinos destino = new ListaDestinos(
                        "Descripción del lugar", // Aquí puedes obtener la descripción de alguna fuente
                        imageUrl,
                        "Localización geográfica", // Aquí puedes obtener la localización de alguna fuente
                        fileUri.getLastPathSegment().replace(".jpg", "").replace(".png", "") // Usando el nombre del archivo como nombre del lugar
                );

                databaseRef.child(destinationId).setValue(destino).addOnSuccessListener(aVoid -> {
                    // Registro creado exitosamente
                    Log.d("Firebase", "Database entry created successfully");
                }).addOnFailureListener(e -> {
                    // Error al crear el registro
                    Log.e("Firebase", "Failed to create database entry", e);
                });
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Failed to get download URL", e);
            });
        }).addOnFailureListener(exception -> {
            // Error al subir
            Log.e("Firebase", "Upload failed", exception);
        });
    }

    private void processImagesFromAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] files = assetManager.list("img");
            if (files != null) {
                mostrarMensaje("Numero de imagenes encontradas: " + files.length);
                for (String filename : files) {
                    InputStream inputStream = assetManager.open("img/" + filename);
                    File tempFile = new File(getCacheDir(), filename);

                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();

                    inputStream.close();

                    // Subir archivo a Firebase Storage
                    uploadToFirebaseStorage(tempFile);
                }
            } else {
                mostrarMensaje("No se encontraron imágenes en la carpeta de assets/img");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cheackAndUploadImages(Context context) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            if (listResult.getItems().isEmpty()) {
                processImagesFromAssets(context);
            } else {
                mostrarMensaje("Ya existen imágenes en Firebase Storage");
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

}
