package ues.alexus21.travelingapp.firebasedatacollection;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
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

import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;

public class FirebaseDataCollection extends AppCompatActivity {
    TextView lblUsuarioLogeado;
    private ILocalUserDAO localUserDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_destinos);

        lblUsuarioLogeado = findViewById(R.id.lblUsuarioLogeado);
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        // Llama a setLoggedUserName para establecer el nombre de usuario
        setLoggedUserName();
    }

    void setLoggedUserName() {
        new GetFirebaseIdTask().execute();
    }

    private class GetFirebaseIdTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return getFirebaseId();
        }

        @Override
        protected void onPostExecute(String firebaseId) {
            String localId = getLocalUserId();
            boolean idsMatch = compareIds(firebaseId, localId);
            updateUI(idsMatch);
        }
    }

    String getFirebaseId() {
        final String[] idFirebase = {null};
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String id = userSnapshot.child("id").getValue(String.class);
                    if (id != null) {
                        Log.d("ID de Firebase", id);
                        idFirebase[0] = id;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error al leer el ID de Firebase", databaseError.toException());
            }
        });

        // Esperar a que el valor se establezca (máximo 5 segundos)
        int retries = 0;
        while (idFirebase[0] == null && retries < 10) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retries++;
        }

        return idFirebase[0];
    }

    String getLocalUserId() {
        String idLocal = localUserDAO.getUserId();
        if (idLocal != null) {
            Log.d("ID Local", idLocal);
        } else {
            Log.d("ID Local", "El ID Local es null");
        }
        return idLocal;
    }

    boolean compareIds(String firebaseId, String localId) {
        if (firebaseId == null) {
            Log.d("ID de Firebase", "El ID de Firebase es null");
            return false;
        }
        return firebaseId.equals(localId);
    }

    void updateUI(boolean idsMatch) {
        if (idsMatch) {
            lblUsuarioLogeado.setText("Usuario Logeado");
        } else {
            lblUsuarioLogeado.setText("Invitado");
        }
    }
}
