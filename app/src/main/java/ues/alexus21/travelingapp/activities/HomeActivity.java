package ues.alexus21.travelingapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.fragments.FavoritosFragment;
import ues.alexus21.travelingapp.fragments.ListaDestinosFragment;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;

public class HomeActivity extends AppCompatActivity {
    TextView lblUsuarioLogeado;
    private ILocalUserDAO localUserDAO;
    CircleImageView imgUsuarioLogeado;

    Fragment listaDestinosFragment, favoritosFragment;
    FragmentContainerView fragmentContainerView;
    BottomNavigationView bottomNavigationView;
    TextView lblnameapp;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Obtener referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        // Llama a setLoggedUserName para establecer el nombre de usuario
        setLoggedUserName(databaseRef, localUserDAO);

        lblUsuarioLogeado = findViewById(R.id.lblUsuarioLogeado);
        imgUsuarioLogeado = findViewById(R.id.imgUsuarioLogeado);

        listaDestinosFragment = new ListaDestinosFragment();
        favoritosFragment = new FavoritosFragment();

        fragmentContainerView = findViewById(R.id.fragmentContainerView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        lblnameapp = findViewById(R.id.lblnameapp);
        lblnameapp.setText("Destinos");

        bottomNavigationView.setOnNavigationItemSelectedListener(this::handleNavigationItemSelected);

        imgUsuarioLogeado.setOnClickListener(v -> {
            Intent userProfileIntent = new Intent(this, UserProfileActivity.class);
            startActivity(userProfileIntent);
            finish();
        });
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private boolean handleNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navPrincipal:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, listaDestinosFragment)
                        .commit();
                lblnameapp.setText("Destinos");
                return true;
            case R.id.navFavoritos:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, favoritosFragment)
                        .commit();
                lblnameapp.setText("Mis Favoritos");
                return true;
            default:
                return false;
        }
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
}
