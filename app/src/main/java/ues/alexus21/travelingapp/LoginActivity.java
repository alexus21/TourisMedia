package ues.alexus21.travelingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    SpannableString spannableString;
    TextView lbl_login_aActivitySignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        lbl_login_aActivitySignup = findViewById(R.id.lbl_login_aActivitySignup);

        String text = "¿No tienes cuenta? ¡Registrate!";
        spannableString = new SpannableString(text);

        int startIndex = 0;
        int endIndex = text.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, 0);
        lbl_login_aActivitySignup.setText(spannableString);
        lbl_login_aActivitySignup.setMovementMethod(LinkMovementMethod.getInstance());

    }
}