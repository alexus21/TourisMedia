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

    void setLoggedUserName() {
        // Obtener referencia a la base de datos de Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        // Leer el ID de Firebase almacenado en la base de datos
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String idFirebase = userSnapshot.child("id").getValue(String.class);
                    if (idFirebase != null) {
                        Log.d("ID de Firebase", idFirebase);

                        // Obtener el ID local
                        String idLocal = localUserDAO.getUserId();
                        if (idLocal != null) {
                            Log.d("ID Local", idLocal);

                            String email = userSnapshot.child("email").getValue(String.class);
                            // Remover todo lo que vaya desde @ (la @ incluida)
                            String name = email.substring(0, email.indexOf('@'));

                            System.out.println(name);

                            // Comparar los IDs
                            if (idFirebase.equals(idLocal)) {
                                lblUsuarioLogeado.setText(name);
                            } else {
                                lblUsuarioLogeado.setText("Invitado");
                            }
                        }
                    } else {
                        lblUsuarioLogeado.setText("Invitado");
                        Log.d("ID de Firebase", "El ID de Firebase es null");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error al leer el ID de Firebase", databaseError.toException());
            }
        });
    }
}
