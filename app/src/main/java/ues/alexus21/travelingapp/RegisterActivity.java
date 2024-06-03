package ues.alexus21.travelingapp;

import android.app.Activity;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.localstorage.LocalUserModel;
import ues.alexus21.travelingapp.user.User;
import ues.alexus21.travelingapp.validations.EncryptPassword;
import ues.alexus21.travelingapp.validations.UserRegistrationValidation;
import ues.alexus21.travelingapp.validations.UserValidator;

public class RegisterActivity extends AppCompatActivity {

    public DatabaseReference reference;
    private FirebaseFirestore db;
    public EditText txtCorreoRegister, txtPasswordRegister, txtRetypePasswordRegister;
    public Button btnSignUp;

    SpannableString spannableString;
    TextView lbl_register_aActivityLogin;
    public ILocalUserDAO localUserDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        txtCorreoRegister = findViewById(R.id.txtCorreoRecover);
        txtPasswordRegister = findViewById(R.id.txtPasswordRecover);
        txtRetypePasswordRegister = findViewById(R.id.txtRetypePasswordRegister);
        btnSignUp = findViewById(R.id.btnRecoverPassword);
        lbl_register_aActivityLogin = findViewById(R.id.lbl_register_aActivityLogin);
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        createSpannableString("¿Ya tienes cuenta? ¡Inicia sesión!", lbl_register_aActivityLogin, LoginActivity.class);

        btnSignUp.setOnClickListener(v -> {
            String email = txtCorreoRegister.getText().toString().trim();
            String password = txtPasswordRegister.getText().toString().trim();
            String retypePassword = txtRetypePasswordRegister.getText().toString().trim();

            boolean isValid = UserValidator.validateRegistration(email, password, retypePassword,
                    txtCorreoRegister, txtPasswordRegister, txtRetypePasswordRegister);

            if (!isValid) {
                return;
            }

            // Deshabilitar el botón para evitar múltiples solicitudes
            btnSignUp.setEnabled(false);

            FirebaseDataCollection.checkEmail(email, exists -> {
                if (exists) {
                    txtCorreoRegister.setError("Este correo ya está en uso. Prueba uno diferente.");
                    btnSignUp.setEnabled(true); // Habilitar el botón si el correo ya está registrado
                } else {
                    registerUser(email, password);
                    Intent listaDestinos = new Intent(RegisterActivity.this, ListaDestinosActivity.class);
                    startActivity(listaDestinos);
                }
            });
        });
    }

    void registerUser(String email, String password) {
        reference = FirebaseDatabase.getInstance().getReference();

        String id = reference.push().getKey();

        password = EncryptPassword.encryptPassword(password);

        User user = new User(id, email, password);

        // Insertar a Firebase:
        reference.child("users").push().setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                });

        // Guardar localmente:
        localUserDAO.insertUser(new LocalUserModel(email, password, 1, id));
    }

    void createSpannableString(String text, TextView item, Class<? extends Activity> targetActivity) {
        spannableString = new SpannableString(text);

        int startIndex = 0;
        int endIndex = text.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent registerIntent = new Intent(RegisterActivity.this, targetActivity);
                startActivity(registerIntent);
                finish();
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0);
        item.setText(spannableString);
        item.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
