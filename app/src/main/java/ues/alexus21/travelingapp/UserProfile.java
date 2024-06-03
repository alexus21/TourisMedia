package ues.alexus21.travelingapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;

public class UserProfile extends AppCompatActivity {

    private ILocalUserDAO localUserDAO;
    ImageView imgAtras;
    Button btnUpdateMyPassword, btnEndSession, btnDeleteMyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        imgAtras = findViewById(R.id.imgAtras);
        btnUpdateMyPassword = findViewById(R.id.btnUpdateMyPassword);
        btnEndSession = findViewById(R.id.btnEndSession);
        btnDeleteMyAccount = findViewById(R.id.btnDeleteMyAccount);

        String userId = localUserDAO.getUserId();

        imgAtras.setOnClickListener(v -> {
            finish();
        });

        btnEndSession.setOnClickListener(v -> {
            localUserDAO.logout(userId);
            redirectToLogin();
            finish();
        });
    }

    void redirectToLogin(){
        // Redirigir al usuario a la pantalla de inicio de sesión
        Intent loginIntent = new Intent(ListaDestinosActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}