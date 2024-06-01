package ues.alexus21.travelingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;

public class MainActivity extends AppCompatActivity {

    public ILocalUserDAO localUserDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Obtener el id de firebase:
        String id = databaseRef.push().getKey();
        if (id != null) {
            Log.d("ID", id);

            // Obtener el id local
            String idLocal = localUserDAO.getUserId();
            if (idLocal != null) {
                Log.d("IDLOCAL", idLocal);

                if (id.equals(idLocal)) {
                    Intent ListaDestinosIntent = new Intent(this, ListaDestinosActivity.class);
                    startActivity(ListaDestinosIntent);
                } else {
                    Intent LoginIntent = new Intent(this, LoginActivity.class);
                    startActivity(LoginIntent);
                }
            } else {
                Log.d("MainActivity", "IDLOCAL es null");
                // Manejar el caso en el que el ID local es null
                Intent LoginIntent = new Intent(this, LoginActivity.class);
                startActivity(LoginIntent);
            }
        } else {
            Log.d("MainActivity", "No se pudo obtener el id de Firebase");
            // Manejar el caso en el que no se pudo obtener el id de Firebase
        }
    }
}
