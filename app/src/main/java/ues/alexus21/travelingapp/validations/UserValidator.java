package ues.alexus21.travelingapp.validations;

import android.widget.EditText;

public class UserValidator {

    public static boolean validateLogin(String email, String password, String retypePassword,
                                        EditText txtCorreoRegister, EditText txtPasswordRegister, EditText txtRetypePasswordRegister) {

        if (!UserRegistrationValidation.validateEmailStructure(email)) {
            txtCorreoRegister.setError("El correo debe ser de un dominio permitido");
            return false;
        }

        if (UserRegistrationValidation.isEmailEmpty(email)) {
            txtCorreoRegister.setError("El correo no puede estar vacío");
            return false;
        }

        if (!UserRegistrationValidation.isEmailValid(email)) {
            txtCorreoRegister.setError("El correo debe tener un @ y un .");
            return false;
        }

        if (UserRegistrationValidation.isPasswordEmpty(password)) {
            txtPasswordRegister.setError("La contraseña no puede estar vacía");
            return false;
        }

        if (!UserRegistrationValidation.isPasswordComplex(password)) {
            txtPasswordRegister.setError("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un caracter especial");
            return false;
        }

        if (!UserRegistrationValidation.validateRetypePassword(password, retypePassword)) {
            txtRetypePasswordRegister.setError("Las contraseñas no coinciden");
            return false;
        }

        return true;
    }

    public static boolean validateLogin(String email, String password,
                                        EditText txtCorreoRegister, EditText txtPasswordRegister) {

        if (email.isEmpty()) {
            txtCorreoRegister.setError("Ingrese su correo electrónico");
            return false;
        }

        if (password.isEmpty()) {
            txtCorreoRegister.setError("Ingrese su correo electrónico");
            return false;
        }

        return true;
    }
}
