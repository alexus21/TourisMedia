package ues.alexus21.travelingapp.validations;

import android.widget.EditText;

public class NewPostValidation {
    public static boolean validateNewPost(String placeName, String placeDescription, String placeLocation,
                                          EditText editTextPlaceName, EditText editTextPlaceDescription, EditText editTextPlaceLocation) {
        if (placeName.isEmpty()) {
            editTextPlaceName.setError("El nombre del lugar es requerido");
            editTextPlaceName.requestFocus();
            return false;
        }

        if (placeDescription.isEmpty()) {
            editTextPlaceDescription.setError("La descripción del lugar es requerida");
            editTextPlaceDescription.requestFocus();
            return false;
        }

        if (placeLocation.isEmpty()) {
            editTextPlaceLocation.setError("La ubicación del lugar es requerida");
            editTextPlaceLocation.requestFocus();
            return false;
        }

        return true;
    }
}
