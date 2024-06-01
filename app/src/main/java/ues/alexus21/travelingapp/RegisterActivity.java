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
import com.google.firebase.firestore.FirebaseFirestore;

import ues.alexus21.travelingapp.user.User;
import ues.alexus21.travelingapp.validations.EncryptPassword;
import ues.alexus21.travelingapp.validations.UserRegistrationValidation;

public class RegisterActivity extends AppCompatActivity {

    public DatabaseReference reference;
    private FirebaseFirestore db;
    public EditText txtCorreoRegister, txtPasswordRegister, txtRetypePasswordRegister;
    public Button btnSignUp;

    SpannableString spannableString;
    TextView lbl_register_aActivityLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        txtCorreoRegister = findViewById(R.id.txtCorreoRegister);
        txtPasswordRegister = findViewById(R.id.txtPasswordRegister);
        txtRetypePasswordRegister = findViewById(R.id.txtRetypePasswordRegister);
        btnSignUp = findViewById(R.id.btnSignUp);
        lbl_register_aActivityLogin = findViewById(R.id.lbl_register_aActivityLogin);

        String text = "¿Tienes cuenta? ¡Ingresa!";
        spannableString = new SpannableString(text);

        int startIndex = 0;
        int endIndex = text.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0);
        lbl_register_aActivityLogin.setText(spannableString);
        lbl_register_aActivityLogin.setMovementMethod(LinkMovementMethod.getInstance());

        btnSignUp.setOnClickListener(v -> {
            String email = txtCorreoRegister.getText().toString();
            String password = txtPasswordRegister.getText().toString();
            String retypePassword = txtRetypePasswordRegister.getText().toString();

            if (!UserRegistrationValidation.validateEmailStructure(email)) {
                Toast.makeText(this, "El correo debe ser de un dominio permitido", Toast.LENGTH_SHORT).show();
                txtCorreoRegister.setError("");
                return;
            }

            if (UserRegistrationValidation.isEmailEmpty(email)) {
                Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show();
                txtCorreoRegister.setError("");
                return;
            }

            if (!UserRegistrationValidation.isEmailValid(email)) {
                Toast.makeText(this, "El correo debe tener un @ y un .", Toast.LENGTH_SHORT).show();
                txtCorreoRegister.setError("");
                return;
            }


            if (UserRegistrationValidation.isPasswordEmpty(password)) {
                Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                txtPasswordRegister.setError("");
                return;
            }

            if (!UserRegistrationValidation.isPasswordComplex(password)) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, " +
                        "un número y un caracter especial", Toast.LENGTH_SHORT).show();
                txtPasswordRegister.setError("");
                return;
            }

            if (!UserRegistrationValidation.validateRetypePassword(password, retypePassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                txtRetypePasswordRegister.setError("");
                return;
            }

            // Deshabilitar el botón para evitar múltiples solicitudes
            btnSignUp.setEnabled(false);

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users");
            databaseRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // El correo electrónico ya existe
                                Toast.makeText(RegisterActivity.this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show();
                                txtCorreoRegister.setError("El correo ya está registrado");
                                btnSignUp.setEnabled(true); // Habilitar el botón si el correo ya está registrado
                            } else {
                                registerUser(email, password);
                                Intent listaDestinos = new Intent(RegisterActivity.this, ListaDestinosActivity.class);
                                startActivity(listaDestinos);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Manejo de errores
                            Toast.makeText(RegisterActivity.this, "Error al verificar el correo electrónico", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    void registerUser(String email, String password) {
        reference = FirebaseDatabase.getInstance().getReference();
        String id = reference.push().getKey();
        password = EncryptPassword.encryptPassword(password);
        User user = new User(id, email, password);
        System.out.println(user);
        reference.child("users").push().setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                });
    }
}
