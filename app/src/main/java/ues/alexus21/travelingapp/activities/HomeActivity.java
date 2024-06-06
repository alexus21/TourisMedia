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
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    String nameApp = "";

    Fragment listaDestinosFragment, favoritosFragment, mainFragment;
    FragmentContainerView fragmentContainerView;
    BottomNavigationView bottomNavigationView;
    TextView lblnameapp;
    FloatingActionButton btnAgregarDestinos;

    @SuppressLint({"SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systembars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systembars.left, systembars.top, systembars.right, systembars.bottom);
            return insets;
        });

        // Obtener referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        // Llama a setLoggedUserName para establecer el nombre de usuario
        setLoggedUserName(databaseRef, localUserDAO);

        lblUsuarioLogeado = findViewById(R.id.lblUsuarioLogeado);
        imgUsuarioLogeado = findViewById(R.id.imgUsuarioLogeado);
        btnAgregarDestinos = findViewById(R.id.btnAgregarDestinos);

        listaDestinosFragment = new ListaDestinosFragment();
        favoritosFragment = new FavoritosFragment();
        mainFragment = null;

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

        btnAgregarDestinos.setOnClickListener(v -> {
            Intent nuevoPostIntent = new Intent(this, NuevoPostActivity.class);
            startActivity(nuevoPostIntent);
        });
    }

    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
    private boolean handleNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navPrincipal:
                btnAgregarDestinos.show();
                mainFragment = new ListaDestinosFragment();
                nameApp = "Destinos";
                break;
            case R.id.navFavoritos:
                btnAgregarDestinos.hide();
                mainFragment = new FavoritosFragment();
                nameApp = "Mis Favoritos";
                break;
        }

        if (mainFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, mainFragment)
                    .commit();
            lblnameapp.setText(nameApp);
        }
        return true;
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
