package ues.alexus21.travelingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    public DatabaseReference reference;
    public EditText txtCorreoRegister, txtPasswordRegister, txtRetypePasswordRegister;
    public Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        txtCorreoRegister = findViewById(R.id.txtCorreoRegister);
        txtPasswordRegister = findViewById(R.id.txtPasswordRegister);
        txtRetypePasswordRegister = findViewById(R.id.txtRetypePasswordRegister);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> {
            String email = txtCorreoRegister.getText().toString();
            String password = txtPasswordRegister.getText().toString();
            String retypePassword = txtRetypePasswordRegister.getText().toString();

            if (!com.ues.tourismedia.validations.UserRegistrationValidation.validateEmail(email)) {
                Toast.makeText(this, "El correo debe ser de un dominio permitido", Toast.LENGTH_SHORT).show();
                txtCorreoRegister.setError("");
                return;
            }

            if (com.ues.tourismedia.validations.UserRegistrationValidation.isPasswordEmpty(password)) {
                Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                txtPasswordRegister.setError("");
                return;
            }

            if (!com.ues.tourismedia.validations.UserRegistrationValidation.isPasswordComplex(password)) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, " +
                        "un número y un caracter especial", Toast.LENGTH_SHORT).show();
                txtPasswordRegister.setError("");
                return;
            }

            if (!com.ues.tourismedia.validations.UserRegistrationValidation.validateRetypePassword(password, retypePassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                txtRetypePasswordRegister.setError("");
                return;
            }

            reference = FirebaseDatabase.getInstance().getReference();
            String id = reference.push().getKey();
            password = com.ues.tourismedia.validations.EncryptPassword.encryptPassword(password);
            com.ues.tourismedia.user.User user = new com.ues.tourismedia.user.User(id, email, password);
            System.out.println(user);
            reference.child("users").push().setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}