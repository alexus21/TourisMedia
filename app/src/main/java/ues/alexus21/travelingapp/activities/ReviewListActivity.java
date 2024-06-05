package ues.alexus21.travelingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ues.alexus21.travelingapp.R;

public class ReviewListActivity extends AppCompatActivity {

    ImageView imgAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_list);

        imgAtras = findViewById(R.id.imgAtras);

        imgAtras.setOnClickListener(v -> {
            Intent placeReviewIntent = new Intent(ReviewListActivity.this, HomeActivity.class);
            startActivity(placeReviewIntent);
            finish();
        });
    }
}
