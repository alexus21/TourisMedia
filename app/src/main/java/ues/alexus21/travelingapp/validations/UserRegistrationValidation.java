package com.ues.tourismedia.validations;

public class UserRegistrationValidation {

    public static boolean validateEmail(String email) {
        return email.contains("@gmail.com") || email.contains("@hotmail.com") ||
                email.contains("@yahoo.com") || email.contains("@outlook.com") ||
                email.contains("@live.com") || email.contains("@icloud.com") ||
                email.contains("@aol.com") || email.contains("@protonmail.com");
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
