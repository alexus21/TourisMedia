package ues.alexus21.travelingapp.firebasedatacollection;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ues.alexus21.travelingapp.user.User;
import ues.alexus21.travelingapp.validations.EncryptPassword;

public class FirebaseDataCollection extends AppCompatActivity {

    static DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users");

    public interface CheckEmailCallback {
        void onCallback(boolean exists);
    }

    public interface LoginCallback {
        void onCallback(boolean success);
    }

    public interface IdFirebaseCallback {
        void onCallback(String firebaseId);
    }

    public interface UpdatePasswordCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void checkEmail(String email, CheckEmailCallback callback) {
        databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();
                callback.onCallback(exists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage());
                callback.onCallback(false); // o manejar el error de otra forma si lo prefieres
            }
        });
    }

    public static void login(String email, String password, LoginCallback callback) {
        databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        User user = childSnapshot.getValue(User.class);
                        String uuid = childSnapshot.getKey();
                        if (user != null) {
                            String hashedPassword = EncryptPassword.encryptPassword(password);
                            if (user.getPassword().equals(hashedPassword)) {
                                callback.onCallback(true);
                                return;
                            }
                        }
                    }
                    callback.onCallback(false);
                } else {
                    callback.onCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", error.getMessage());
                callback.onCallback(false);
            }
        });
    }

    public static void obtenerIdFirebase(String email, IdFirebaseCallback callback) {
        databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String idFirebase = userSnapshot.child("id").getValue(String.class);
                    callback.onCallback(idFirebase);
                    return;
                }
                callback.onCallback(null); // No se encontró el email
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error al leer el ID de Firebase", databaseError.toException());
                callback.onCallback(null);
            }
        });
    }

    public static void updateUserPassword(String email, String password, UpdatePasswordCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        String hashedPassword = EncryptPassword.encryptPassword(password);

        databaseRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String userId = childSnapshot.getKey();
                        if (userId != null) {
                            DatabaseReference userRef = databaseRef.child(userId);
                            userRef.child("password").setValue(hashedPassword)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure(e));
                            return;
                        }
                    }
                } else {
                    callback.onFailure(new Exception("Usuario no encontrado"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }
}
