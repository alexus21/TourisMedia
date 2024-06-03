package ues.alexus21.travelingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.localstorage.LocalUserModel;
import ues.alexus21.travelingapp.validations.UserValidator;

public class LoginActivity extends AppCompatActivity {

    SpannableString spannableString;
    TextView lbl_login_aActivitySignup, lbl_password_forgotten;

    EditText txtUsuarioLogin, txtConstrasenaLogin;
    Button btn_ingresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        lbl_login_aActivitySignup = findViewById(R.id.lbl_login_aActivitySignup);
        lbl_password_forgotten = findViewById(R.id.lbl_password_forgotten);
        txtUsuarioLogin = findViewById(R.id.txtUsuarioLogin);
        txtConstrasenaLogin = findViewById(R.id.txtConstrasenaLogin);
        btn_ingresar = findViewById(R.id.btn_ingresar);

        createSpannableString("¿No tienes cuenta? ¡Regístrate!", lbl_login_aActivitySignup, RegisterActivity.class);
        createSpannableString("¿Olvidaste tu contraseña?", lbl_password_forgotten, PasswordForgottenActivity.class);

        btn_ingresar.setOnClickListener(v -> {
            String email = txtUsuarioLogin.getText().toString().trim();
            String password = txtConstrasenaLogin.getText().toString().trim();

            boolean isValid = UserValidator.validateRegistration(email, password,
                    txtUsuarioLogin, txtConstrasenaLogin);

            if (!isValid) {
                return;
            }

            FirebaseDataCollection.checkEmail(email, exists -> {
                if (exists) {
                    FirebaseDataCollection.login(email, password, (success) -> {
                        if (success) {
                            iniciarSesion(email, password);
                        } else {
                            mostrarMensajeError("Correo o contraseña incorrectos");
                        }
                    });
                } else {
                    mostrarMensajeError("Correo o contraseña incorrectos");
                }
            });
        });
    }

    private void iniciarSesion(String email, String password) {
        FirebaseDataCollection.obtenerIdFirebase(email, firebaseId -> {
            if (firebaseId != null) {
                manejarSesion(email, password, firebaseId);
            } else {
                mostrarMensajeError("Error al obtener el ID de Firebase");
            }
        });
    }

    private void manejarSesion(String email, String password, String firebaseId) {
        ILocalUserDAO localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        if (localUserDAO.checkIfExist(email) == null) {
            Log.d("Manejo de Sesión", "Insertando usuario: correo: " + email + " clave: " + password + " estado: 1 id remoto: " + firebaseId);
            localUserDAO.insertUser(new LocalUserModel(email, password, 1, firebaseId));
        } else {
//            String localUserId = localUserDAO.getUserId(firebaseId);
            Log.d("Manejo de Sesión", "Actualizando estado de usuario: " + email);
            localUserDAO.updateUser(email);
        }

        iniciarListaDestinosActivity();
    }

    private void iniciarListaDestinosActivity() {
        Intent intent = new Intent(LoginActivity.this, ListaDestinosActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMensajeError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    void createSpannableString(String text, TextView item, Class<? extends Activity> targetActivity) {
        spannableString = new SpannableString(text);

        int startIndex = 0;
        int endIndex = text.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent registerIntent = new Intent(LoginActivity.this, targetActivity);
                startActivity(registerIntent);
                finish();
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0);
        item.setText(spannableString);
        item.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
