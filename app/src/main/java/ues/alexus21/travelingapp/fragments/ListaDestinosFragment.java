package ues.alexus21.travelingapp.fragments;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.adapters.ListaDestinosAdapter;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.localstorage.LocalUserModel;
import ues.alexus21.travelingapp.models.ListaDestinos;
import ues.alexus21.travelingapp.validations.NetworkChecker;

public class ListaDestinosFragment extends Fragment {

    private ListView ltsDestinosTuristicos;
    private ListaDestinosAdapter adapter;
    private ArrayList<ListaDestinos> listaDestinos;
    private ILocalUserDAO localUserDAO;

    public ListaDestinosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lista_destinos, container, false);

        // Obtener referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        localUserDAO = DatabaseSingleton.getDatabase(requireContext()).localUserDAO();

        ltsDestinosTuristicos = root.findViewById(R.id.ltsDestinosTuristicos);
        listaDestinos = new ArrayList<>();

        if (NetworkChecker.checkInternetConnection(requireContext())) {
            mostrarMensaje("No hay conexión a internet");
        }

        DatabaseReference destinationReference = FirebaseDatabase.getInstance().getReference("destination");
        destinationReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaDestinos.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("ListaDestinosFragment", "Destino full: " + dataSnapshot.getKey());
                    ListaDestinos destino = dataSnapshot.getValue(ListaDestinos.class);
                    listaDestinos.add(destino);
                    Log.d("ListaDestinosFragment", "Destino: " + destino.getId_destination());
                }
                adapter = new ListaDestinosAdapter(listaDestinos, requireContext(), localUserDAO.getUserId());
                ltsDestinosTuristicos.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarMensaje("Error al cargar los destinos turísticos");
            }
        });

        String userId = localUserDAO.getUserId();

        // Imprimir todo lo de localUserDAO.getUser
        for (LocalUserModel user : localUserDAO.getAll()) {
            Log.d("ListaDestinosFragment", "ID: " + user.getId());
            Log.d("ListaDestinosFragment", "Email: " + user.getEmail());
            Log.d("ListaDestinosFragment", "Password: " + user.getPassword());
            Log.d("ListaDestinosFragment", "Remote ID: " + user.getUser_remote_id());
            Log.d("ListaDestinosFragment", "Estado: " + user.getIsLogged());
        }

        /*checkAndUploadImages();*/

        return root;
    }

    private void uploadToFirebaseStorage(File file, ListaDestinos destinoAux) {
        Uri fileUri = Uri.fromFile(file);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(fileUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Obtener la URL de descarga de la imagen subida
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Crear un nuevo registro en Firebase Realtime Database
                String imageUrl = uri.toString();
                String destinationId = UUID.randomUUID().toString();

                String uuid = FirebaseDatabase.getInstance().getReference().push().getKey();

                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("destination");

                ListaDestinos destino = new ListaDestinos(
                        destinoAux.getDescription(),
                        imageUrl,
                        destinoAux.getLocation(),
                        destinoAux.getName(),
                        uuid,
                        uuid
                );

                databaseRef.child(uuid).setValue(destino).addOnSuccessListener(aVoid -> {
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

    private void processImagesFromAssets() {
        AssetManager assetManager = requireContext().getAssets();
        try {
            String[] files = assetManager.list("img");
            ArrayList<ListaDestinos> destinos = getDestinos();
            if (files != null) {
                mostrarMensaje("Número de imágenes encontradas: " + files.length);
                int i = 0;
                for (String filename : files) {
                    InputStream inputStream = assetManager.open("img/" + filename);
                    File tempFile = new File(requireContext().getCacheDir(), filename);

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
                    uploadToFirebaseStorage(tempFile, destinos.get(i));
                    i++;
                }
            } else {
                mostrarMensaje("No se encontraron imágenes en la carpeta de assets/img");
            }
        } catch (IOException e) {
            Log.e("ListaDestinosFragment", "Error al procesar imágenes desde assets", e);
        }
    }

    private void checkAndUploadImages() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images");

        storageRef.listAll().addOnSuccessListener(listResult -> {
            if (listResult.getItems().isEmpty()) {
                processImagesFromAssets();
            } else {
                mostrarMensaje("Ya existen imágenes en Firebase Storage");
            }
        }).addOnFailureListener(e -> {
            Log.e("ListaDestinosFragment", "Error al listar imágenes en Firebase Storage", e);
        });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<ListaDestinos> getDestinos() {

        ArrayList<ListaDestinos> listaDestinos = new ArrayList<>();

        //Destino 1
        /*listaDestinos.add(new ListaDestinos(
                "Parte importante de Estambul, desprendiendo belleza por dentro y por " +
                        "fuera",
                "Estambul",
                "Mezquita azul"));

        listaDestinos.add(new ListaDestinos("Es una auténtica joya de la naturaleza y uno de los " +
                "ríos mas bonitos del mundo", "Colombia", "Caño Cristales"));

        listaDestinos.add(new ListaDestinos("Es uo de los desiertos más grandes de este pais",
                "Colombia",
                "Desierto de la Tatacoa"));

        listaDestinos.add(new ListaDestinos("Una muralla que se construyó hace miles de años para " +
                "defender al imperio chino de constantes invasiones, y que ahora se encuentra entre " +
                "las 7 maravillas del mundo moderno",
                "China",
                "La Gran Muralla China"));

        listaDestinos.add(new ListaDestinos("Estas impresionantes cataratas se encuentran en la " +
                "frontera natural entre Zambia y Zimbawe, y están consideradas como una de las más " +
                "grandes del mundo",
                "Zambia",
                "Cataratas Victoria"));

        listaDestinos.add(new ListaDestinos("Una de las 7 maravillas del mundo actualmente, es una " +
                "impresionante ciudadela inca con templos, andenes y canales de agua",
                "Perú", "Macchu Picchu"));

        listaDestinos.add(new ListaDestinos("¿Qué es un lugar turístico sino una oportunidad de " +
                "vivir una cultura completamente distinta de primera mano?",
                "Dubai", "Dubai"));*/

        return listaDestinos;
    }
}
