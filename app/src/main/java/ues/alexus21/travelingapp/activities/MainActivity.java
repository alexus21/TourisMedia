package ues.alexus21.travelingapp.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;

public class MainActivity extends AppCompatActivity {

    private ILocalUserDAO localUserDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Validar si ya existe un usuario logueado

        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        String userId = localUserDAO.getUserId();

        if (userId != null) {
            Intent intent = new Intent(this, ListaDestinosActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
