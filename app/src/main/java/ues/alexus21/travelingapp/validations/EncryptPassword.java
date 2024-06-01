package ues.alexus21.travelingapp.validations;

import java.security.MessageDigest;

public class EncryptPassword {
    // Metodo para retornar la contraseña del usuario encryptada
    public static String encryptPassword(String password) {
        try {
            return PasswordChecker.sha1(password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
