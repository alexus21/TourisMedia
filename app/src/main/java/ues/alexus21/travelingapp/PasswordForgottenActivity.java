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
import ues.alexus21.travelingapp.validations.UserValidator;

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
            String email = txtCorreoRecover.getText().toString().trim();
            String password = txtPasswordRecover.getText().toString().trim();
            String retypePassword = txtRetypePasswordRegister.getText().toString().trim();

            boolean isValid = UserValidator.validateRegistration(email, password, retypePassword,
                    txtCorreoRecover, txtPasswordRecover, txtRetypePasswordRegister);

            if (!isValid) {
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
