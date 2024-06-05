package ues.alexus21.travelingapp.activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import ues.alexus21.travelingapp.R;

public class PlaceReviewActivity extends AppCompatActivity {

    CircleImageView imgDestino;
    ImageView imgAtras;
    RatingBar ratingBar;
    Button btnSetRating;
    TextView textViewDestinyName, textViewDescription, textViewLocation;
    EditText editTextAddComments;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_review);

        String imageUrl = getIntent().getStringExtra("imageUrl");
        Log.d("PlaceReviewActivity", "Image URL: " + imageUrl);


        imgDestino = findViewById(R.id.imgDestino);
        imgAtras = findViewById(R.id.imgAtras);
        ratingBar = findViewById(R.id.ratingBar);
        btnSetRating = findViewById(R.id.btnSetRating);
        textViewDestinyName = findViewById(R.id.textViewDestinyName);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewLocation = findViewById(R.id.textViewLocation);
        editTextAddComments = findViewById(R.id.editTextAddComments);

        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1.0f);

        imgAtras.setOnClickListener(v -> {
            Intent listaDestinosActivity = new Intent(PlaceReviewActivity.this, HomeActivity.class);
            startActivity(listaDestinosActivity);
        });

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(imgDestino);
        } else {
            Toast.makeText(this, "URL de la imagen no encontrada", Toast.LENGTH_SHORT).show();
        }

        textViewDestinyName.setText("Nombre del destino: " + getIntent().getStringExtra("imageUrl"));
        textViewDescription.setText(getIntent().getStringExtra("placeDescription"));
        textViewLocation.setText("Ubicación: " + getIntent().getStringExtra("placeLocation"));

        // Asignar el rating a la actividad
        float rating = getIntent().getFloatExtra("rating", 0);
        ratingBar.setRating(rating);
        btnSetRating.setOnClickListener(v -> {
            Toast.makeText(PlaceReviewActivity.this, "Rating: " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();

            String comments = editTextAddComments.getText().toString();
            editTextAddComments.setText(getIntent().getStringExtra("imageUrl"));
            // Sin evento de comentario vacío, porque puede ser que el usuario ingrese solo el rating sin el comentario
        });


    }
}
