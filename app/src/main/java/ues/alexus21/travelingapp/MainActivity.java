package ues.alexus21.travelingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ILocalUserDAO localUserDAO;

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

        // Inicializar el DAO
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        // Obtener referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Leer el ID de Firebase almacenado en la base de datos
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String idFirebase = userSnapshot.child("id").getValue(String.class);
                    if (idFirebase != null) {
                        Log.d(TAG, "ID de Firebase: " + idFirebase);

                        // Obtener el ID local
                        String idLocal = localUserDAO.getUserId();
                        if (idLocal != null) {
                            Log.d(TAG, "ID Local: " + idLocal);

                            // Comparar los IDs
                            if (idFirebase.equals(idLocal)) {
                                navigateToActivity(ListaDestinosActivity.class);
                            } else {
                                navigateToActivity(LoginActivity.class);
                            }
                        } else {
                            Log.d(TAG, "IDLOCAL es null");
                            navigateToActivity(LoginActivity.class);
                        }
                    } else {
                        Log.d(TAG, "El ID de Firebase es null");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error al leer el ID de Firebase", databaseError.toException());
            }
        });
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
        finish();
    }
}
