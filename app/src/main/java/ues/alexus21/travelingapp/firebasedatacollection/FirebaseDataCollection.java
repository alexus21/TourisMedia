package ues.alexus21.travelingapp.firebasedatacollection;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDataCollection extends AppCompatActivity {

    static DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users");

    public interface CheckEmailCallback {
        void onCallback(boolean exists);
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
}
