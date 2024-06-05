package ues.alexus21.travelingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import ues.alexus21.travelingapp.R;

public class NuevoPostActivity extends AppCompatActivity {

    ImageView imgAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_post);

        imgAtras = findViewById(R.id.imgAtras);

        imgAtras.setOnClickListener(v -> {
            startActivity(new Intent(NuevoPostActivity.this, HomeActivity.class));
            finish();
        });
    }
}
