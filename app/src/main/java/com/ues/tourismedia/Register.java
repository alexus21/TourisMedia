package com.ues.tourismedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class Register extends AppCompatActivity {

    public EditText txtNombreRegister, txtEmailRegister, txtPasswordRegister, txtPasswordRegister2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
