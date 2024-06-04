package ues.alexus21.travelingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.validations.EncryptPassword;
import ues.alexus21.travelingapp.validations.NetworkChecker;
import ues.alexus21.travelingapp.validations.UserValidator;

public class UserProfileActivity extends AppCompatActivity {

    private ILocalUserDAO localUserDAO;
    ImageView imgAtras;
    Button btnUpdateMyPassword, btnEndSession, btnDeleteMyAccount;
    EditText txtEmail, txtPassword, txtRetypedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        imgAtras = findViewById(R.id.imgAtras);
        btnUpdateMyPassword = findViewById(R.id.btnSetRating);
        btnEndSession = findViewById(R.id.btnEndSession);
        btnDeleteMyAccount = findViewById(R.id.btnDeleteMyAccount);
        txtEmail = findViewById(R.id.editTextAddComments);
        txtPassword = findViewById(R.id.txtPassword);
        txtRetypedPassword = findViewById(R.id.txtRetypedPassword);

        String userId = localUserDAO.getUserId();
        txtEmail.setText(localUserDAO.loggedEmail());
        // Hacer el EditText no editable
        txtEmail.setFocusable(false);
        txtEmail.setFocusableInTouchMode(false);
        txtEmail.setClickable(false);
        txtEmail.setLongClickable(false);

        imgAtras.setOnClickListener(v -> {
            startActivity(new Intent(UserProfileActivity.this, ListaDestinosActivity.class));
        });

        btnEndSession.setOnClickListener(v -> {
            if(NetworkChecker.checkInternetConnection(this)) {
                mostrarMensaje("No hay conexión a internet");
                return;
            }

            localUserDAO.logout(userId);
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
        });

        btnDeleteMyAccount.setOnClickListener(v -> {
            if(NetworkChecker.checkInternetConnection(this)) {
                mostrarMensaje("No hay conexión a internet");
                return;
            }

            showConfirmationDialog(userId);
        });

        btnUpdateMyPassword.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();
            String retypePassword = txtRetypedPassword.getText().toString().trim();

            if(NetworkChecker.checkInternetConnection(this)) {
                mostrarMensaje("No hay conexión a internet");
                return;
            }

            boolean isValid = UserValidator.validateRegistration(email, password, retypePassword, txtEmail, txtPassword, txtRetypedPassword);

            if (!isValid) {
                return;
            }

            btnUpdateMyPassword.setEnabled(false);

            localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

            FirebaseDataCollection.updateUserPassword(email, password, new FirebaseDataCollection.UpdatePasswordCallback() {
                @Override
                public void onSuccess() {
                    localUserDAO.updateUserPassword(email, EncryptPassword.encryptPassword(password));
                    mostrarMensaje("Contraseña actualizada correctamente");
                }

                @Override
                public void onFailure(Exception e) {
                    mostrarMensaje("Error al actualizar la contraseña");
                }
            });

            localUserDAO.updateUserPassword(email, EncryptPassword.encryptPassword(password));
            startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
        });
    }

    private void showConfirmationDialog(String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta?");
        System.out.println("ID del usuario: " + userId);

        // Botón de confirmación
        builder.setPositiveButton("Sí, eliminar", (dialog, which) -> {
            localUserDAO.delete(userId);
            FirebaseDataCollection.deleteUserById(userId, new FirebaseDataCollection.DeleteUserCallback() {
                @Override
                public void onSuccess() {
                    mostrarMensaje("Cuenta eliminada correctamente");
                    startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    mostrarMensaje("Ocurió un error al eliminar tu cuenta: ");
                    System.out.println("Error al eliminar cuenta: " + e.getMessage());
                }
            });
        });

        // Botón de cancelación
        builder.setNegativeButton("No, cancelar", (dialog, which) -> dialog.dismiss());

        // Crear y mostrar el AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
