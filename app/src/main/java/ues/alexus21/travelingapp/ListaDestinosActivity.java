package ues.alexus21.travelingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.localstorage.LocalUserModel;

public class ListaDestinosActivity extends AppCompatActivity {
    TextView lblUsuarioLogeado;
    private ILocalUserDAO localUserDAO;
    CircleImageView imgUsuarioLogeado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_destinos);

        // Obtener referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        lblUsuarioLogeado = findViewById(R.id.lblUsuarioLogeado);
        imgUsuarioLogeado = findViewById(R.id.imgUsuarioLogeado);

        String userId = localUserDAO.getUserId();

        // Imprimir todo lo de localUserDAO.getUser
        for (LocalUserModel user : localUserDAO.getAll()) {
            System.out.println("ID: " + user.getId());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Password: " + user.getPassword());
            System.out.println("Remote ID: " + user.getUser_remote_id());
            System.out.println("Estado: " + user.getIsLogged());
        }

        imgUsuarioLogeado.setOnClickListener(v -> {
            // Si el usuario hace clic en la imagen, se cierra la sesión
            localUserDAO.logout(userId);
            redirectToLogin();
        });

        // Llama a setLoggedUserName para establecer el nombre de usuario
        setLoggedUserName(databaseRef, localUserDAO);
    }

    @SuppressLint("SetTextI18n")
    void setLoggedUserName(DatabaseReference databaseRef, ILocalUserDAO localUserDAO) {

        String userId = localUserDAO.getUserId();
        System.out.println("User ID: " + userId);
        String email = localUserDAO.getEmailFromRemoteId(userId);
        System.out.println("Este email obtuve: " + email);

        /*String email = localUserDAO.checkIfExist();
        System.out.println("Este email obtuve: " + email);*/

        // Leer el ID de Firebase almacenado en la base de datos
        FirebaseDataCollection.obtenerIdFirebase(email, firebaseId -> {
            if (firebaseId != null) {
                lblUsuarioLogeado.setText(email.substring(0, email.indexOf("@")));
            } else {
                lblUsuarioLogeado.setText("Invitado");
            }
        });
    }

    void redirectToLogin(){
        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent loginIntent = new Intent(ListaDestinosActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
