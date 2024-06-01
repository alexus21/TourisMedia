package ues.alexus21.travelingapp.validations;

import com.google.firebase.firestore.FirebaseFirestore;

public class UserRegistrationValidation {

    public static boolean validateEmailStructure(String email) {
        return EmailChecker.validDomainEmail(email);
    }

    public static boolean isEmailEmpty(String email) {
        return EmailChecker.isEmailEmpty(email);
    }

    public static boolean isEmailValid(String email) {
        return EmailChecker.isEmailValid(email);
    }

    public static boolean isPasswordComplex(String password) {
        return PasswordChecker.isPasswordComplex(password);
    }

    public static boolean isPasswordEmpty(String password) {
        return password.isEmpty();
    }

    public static boolean validateRetypePassword(String password, String retypePassword) {
        return password.equals(retypePassword);
    }
}
