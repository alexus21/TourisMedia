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

import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.localstorage.LocalUserModel;
import ues.alexus21.travelingapp.user.User;
import ues.alexus21.travelingapp.validations.EncryptPassword;
import ues.alexus21.travelingapp.validations.UserRegistrationValidation;

public class PasswordForgottenActivity extends AppCompatActivity {

    EditText txtCorreoRecover, txtPasswordRecover, txtRetypePasswordRegister;
    Button btnRecoverPassword;
    TextView lbl_cancel_action;
    SpannableString spannableString;
    public DatabaseReference reference;
    public ILocalUserDAO localUserDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_forgotten);

        txtCorreoRecover = findViewById(R.id.txtCorreoRecover);
        txtPasswordRecover = findViewById(R.id.txtPasswordRecover);
        txtRetypePasswordRegister = findViewById(R.id.txtRetypePasswordRegister);
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword);
        lbl_cancel_action = findViewById(R.id.lbl_cancel_action);

        createSpannableString("Cancelar", lbl_cancel_action, LoginActivity.class);

        btnRecoverPassword.setOnClickListener(v -> {
            String email = txtCorreoRecover.getText().toString();
            String password = txtPasswordRecover.getText().toString();
            String retypePassword = txtRetypePasswordRegister.getText().toString();

            if (!UserRegistrationValidation.validateEmailStructure(email)) {
                Toast.makeText(this, "El correo debe ser de un dominio permitido", Toast.LENGTH_SHORT).show();
//                txtCorreoRecover.setError("El correo debe ser de un dominio permitido");
                return;
            }

            if (UserRegistrationValidation.isEmailEmpty(email)) {
                Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show();
//                txtCorreoRecover.setError("El correo no puede estar vacío");
                return;
            }

            if (!UserRegistrationValidation.isEmailValid(email)) {
                Toast.makeText(this, "El correo debe tener un @ y un .", Toast.LENGTH_SHORT).show();
//                txtCorreoRecover.setError("El correo debe tener un @ y un .");
                return;
            }

            if (UserRegistrationValidation.isPasswordEmpty(password)) {
                Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show();
                txtPasswordRecover.setError("");
                return;
            }

            if (!UserRegistrationValidation.isPasswordComplex(password)) {
                Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, " +
                        "un número y un caracter especial", Toast.LENGTH_SHORT).show();
                txtPasswordRecover.setError("");
                return;
            }

            if (!UserRegistrationValidation.validateRetypePassword(password, retypePassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                txtRetypePasswordRegister.setError("");
                return;
            }

            // Deshabilitar el botón para evitar múltiples solicitudes
            btnRecoverPassword.setEnabled(false);

            FirebaseDataCollection.checkEmail(email, exists -> {
                if (exists) {
                    updateUserPassword(email, password);
                    startActivity(new Intent(PasswordForgottenActivity.this, LoginActivity.class));
                    finish();
                } else {
                    txtCorreoRecover.setError("El correo proporcionado no se ha encontrado");
                    btnRecoverPassword.setEnabled(true);
                }
            });
        });
    }

    void updateUserPassword(String email, String password) {
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        FirebaseDataCollection.updateUserPassword(email, password, new FirebaseDataCollection.UpdatePasswordCallback() {
            @Override
            public void onSuccess() {
                localUserDAO.updateUserPassword(email, EncryptPassword.encryptPassword(password));
                Toast.makeText(PasswordForgottenActivity.this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(PasswordForgottenActivity.this, "Error al actualizar la contraseña: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        localUserDAO.updateUserPassword(email, EncryptPassword.encryptPassword(password));
    }


    void createSpannableString(String text, TextView item, Class<? extends Activity> targetActivity) {
        spannableString = new SpannableString(text);

        int startIndex = 0;
        int endIndex = text.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent registerIntent = new Intent(PasswordForgottenActivity.this, targetActivity);
                startActivity(registerIntent);
                finish();
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0);
        item.setText(spannableString);
        item.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
