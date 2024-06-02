package ues.alexus21.travelingapp;

import android.annotation.SuppressLint;
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

import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;

public class ListaDestinosActivity extends AppCompatActivity {
    TextView lblUsuarioLogeado;
    private ILocalUserDAO localUserDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_destinos);

        lblUsuarioLogeado = findViewById(R.id.lblUsuarioLogeado);

        // Llama a setLoggedUserName para establecer el nombre de usuario
        setLoggedUserName();
    }

    @SuppressLint("SetTextI18n")
    void setLoggedUserName() {
        // Obtener referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();
        String email = localUserDAO.checkIfExist();
        System.out.println("Email: " + email);

        // Leer el ID de Firebase almacenado en la base de datos
        FirebaseDataCollection.obtenerIdFirebase(email, firebaseId -> {
            if (firebaseId != null) {
                lblUsuarioLogeado.setText(email.substring(0, email.indexOf("@")));
            } else {
                lblUsuarioLogeado.setText("Invitado");
            }
        });
    }
}
