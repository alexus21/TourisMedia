package ues.alexus21.travelingapp.activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.Rating;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Comment;

import de.hdodenhof.circleimageview.CircleImageView;
import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.comments.Comments;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.ratings.Ratings;
import ues.alexus21.travelingapp.validations.NetworkChecker;

public class PlaceReviewActivity extends AppCompatActivity {

    ImageView imgDestino;
    ImageView imgAtras;
    RatingBar ratingBar;
    Button btnSetRating;
    TextView textViewDestinyName, textViewDescription, textViewLocation, textViewShowCommentsAndReviewList, textViewShowPublishedBy;
    EditText editTextAddComments;
    SpannableString spannableString;
    DatabaseReference reference;
    ILocalUserDAO localUserDAO;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_review);

        String imageUrl = getIntent().getStringExtra("imageUrl");
        Log.d("PlaceReviewActivity", "Image URL: " + imageUrl);

        String imageId = getIntent().getStringExtra("placeId");
        Log.d("PlaceReviewActivity", "Image ID: " + imageId);

        imgDestino = findViewById(R.id.imgDestino);
        imgAtras = findViewById(R.id.imgAtras);
        ratingBar = findViewById(R.id.ratingBar);
        btnSetRating = findViewById(R.id.btnSetRating);
        textViewDestinyName = findViewById(R.id.textViewDestinyName);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewLocation = findViewById(R.id.textViewLocation);
        editTextAddComments = findViewById(R.id.editTextAddComments);
        textViewShowCommentsAndReviewList = findViewById(R.id.textViewShowCommentsAndReviewList);
        textViewShowPublishedBy = findViewById(R.id.textViewShowPublishedBy);
        localUserDAO = DatabaseSingleton.getDatabase(this).localUserDAO();

        createSpannableString("Mostrar comentarios", textViewShowCommentsAndReviewList, ReviewListActivity.class);

        String publishedEmail = getIntent().getStringExtra("publisherBy");
        textViewShowPublishedBy.setText("Publicado por: " + publishedEmail);

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

        textViewDestinyName.setText("Nombre del destino: " + getIntent().getStringExtra("placeName"));
        textViewDescription.setText(getIntent().getStringExtra("placeDescription"));
        textViewLocation.setText("Ubicación: " + getIntent().getStringExtra("placeLocation"));

        // Asignar el rating a la actividad
        float rating = getIntent().getFloatExtra("rating", 0);
        ratingBar.setRating(rating);

        btnSetRating.setOnClickListener(v -> {
            String comments = editTextAddComments.getText().toString().trim();
            int ratingValue = (int) ratingBar.getRating();

            if(NetworkChecker.checkInternetConnection(this)) {
                mostrarMensaje("No hay conexión a internet");
                return;
            }

            String firebaseUserId = localUserDAO.getUserId();
            saveComments(comments, firebaseUserId, imageId);
            saveRating(ratingValue, firebaseUserId, imageId);

            Intent homeIntent = new Intent(PlaceReviewActivity.this, HomeActivity.class);
            startActivity(homeIntent);

            // Sin evento de comentario vacío, porque puede ser que el usuario ingrese solo el rating sin el comentario
        });


    }

    void createSpannableString(String text, TextView item, Class<? extends Activity> targetActivity) {
        spannableString = new SpannableString(text);

        int startIndex = 0;
        int endIndex = text.length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent registerIntent = new Intent(PlaceReviewActivity.this, targetActivity);
                startActivity(registerIntent);
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE); // Set the color to white
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        item.setText(spannableString);
        item.setMovementMethod(LinkMovementMethod.getInstance());
    }

    void saveComments(String comments, String userFirebaseId, String imageId){
        reference = FirebaseDatabase.getInstance().getReference();

        Comments userComments = new Comments(comments, userFirebaseId, imageId);
        System.out.println("ImageID: " + imageId);

        reference.child("comments").push().setValue(userComments)
                .addOnSuccessListener(aVoid -> {
                    mostrarMensaje("Comentario guardado correctamente");
                })
                .addOnFailureListener(e -> {
                    mostrarMensaje("Error al guardar comentario");
                });
    }

    void saveRating(float rating, String userFirebaseId, String imageId){
        reference = FirebaseDatabase.getInstance().getReference();

        Ratings userRating = new Ratings(rating, userFirebaseId, imageId);
        System.out.println("ImageID: " + imageId);

        reference.child("ratings").push().setValue(userRating)
                .addOnSuccessListener(aVoid -> {
                    mostrarMensaje("Comentario guardado correctamente");
                })
                .addOnFailureListener(e -> {
                    mostrarMensaje("Error al guardar comentario");
                });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
