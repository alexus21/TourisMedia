package ues.alexus21.travelingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.localstorage.LocalUserModel;
import ues.alexus21.travelingapp.user.User;
import ues.alexus21.travelingapp.validations.EncryptPassword;

public class LoginActivity extends AppCompatActivity {

    SpannableString spannableString;
    TextView lbl_login_aActivitySignup;

    EditText txtUsuarioLogin, txtConstrasenaLogin;
    Button btn_ingresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        lbl_login_aActivitySignup = findViewById(R.id.lbl_login_aActivitySignup);
        txtUsuarioLogin = findViewById(R.id.txtUsuarioLogin);
        txtConstrasenaLogin = findViewById(R.id.txtConstrasenaLogin);
        btn_ingresar = findViewById(R.id.btn_ingresar);

        String text = "¿No tienes cuenta? ¡Registrate!";
        spannableString = new SpannableString(text);

        int startIndex = 0;
        int endIndex = text.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0);
        lbl_login_aActivitySignup.setText(spannableString);
        lbl_login_aActivitySignup.setMovementMethod(LinkMovementMethod.getInstance());

        btn_ingresar.setOnClickListener(v -> {
            String email = txtUsuarioLogin.getText().toString().trim();
            String password = txtConstrasenaLogin.getText().toString().trim();

            // Validar que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
                txtUsuarioLogin.setError("");
                txtConstrasenaLogin.setError("");
                return;
            }

            // Comparar el email y la contraseña con los datos almacenados en firebase
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
            databaseRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // El email existe, verificar la contraseña
                                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                    User user = childSnapshot.getValue(User.class);
                                    String uuid = childSnapshot.getKey();
                                    if (user != null) {
                                        String hashedPassword = EncryptPassword.encryptPassword(password);
                                        if (user.getPassword().equals(hashedPassword)) {
                                            // Las credenciales son correctas, iniciar sesión
                                            iniciarSesion(email, password, uuid);
                                            return;
                                        }
                                    }
                                }
                                // Las credenciales no son correctas
                                mostrarMensajeError("Correo o contraseña incorrectos");
                            } else {
                                // El email no existe
                                mostrarMensajeError("Correo o contraseña incorrectos");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Manejo de errores
                            mostrarMensajeError("Error al verificar las credenciales");
                        }
                    });
        });
    }

    private void iniciarSesion(String email, String password, String id) {

        ILocalUserDAO localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();
        localUserDAO.insertUser(new LocalUserModel(email, password, 1, id));
        Intent intent = new Intent(LoginActivity.this, ListaDestinosActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMensajeError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
