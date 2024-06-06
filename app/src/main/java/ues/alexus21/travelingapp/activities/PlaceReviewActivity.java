package ues.alexus21.travelingapp.activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import de.hdodenhof.circleimageview.CircleImageView;
import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.comments.Comments;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.models.Comentarios;
import ues.alexus21.travelingapp.models.Rating;
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

        String destinationId = getIntent().getStringExtra("destinationId");

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
        ratingBar.setRating(1.0f);

        String firebaseUserId = localUserDAO.getUserId();
        Log.d("Tag", "Rating método: " + getRatingFirebase(destinationId, firebaseUserId));
        Log.d("Tag", "id destino: " + destinationId);
        Log.d("Tag", "id usuario: " + firebaseUserId);

        getRatingFirebase(destinationId, firebaseUserId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Float rating = task.getResult();
                Log.d("Tag", "valor retornado: " + rating);
                if (rating <= 5) {
                    ratingBar.setRating(rating);
                }
            } else {
                Log.e("Tag", "Error al obtener el rating", task.getException());
                Toast.makeText(PlaceReviewActivity.this, "Error al obtener el rating", Toast.LENGTH_SHORT).show();
            }
        });

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating < 1.0f) {
                ratingBar.setRating(1.0f);
            }
        });

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

        /*// Asignar el rating a la actividad
        float rating = getIntent().getFloatExtra("rating", 0);
        ratingBar.setRating(rating);*/

        btnSetRating.setOnClickListener(v -> {
            String comments = editTextAddComments.getText().toString().trim();
            int ratingValue = (int) ratingBar.getRating();

            if (NetworkChecker.checkInternetConnection(this)) {
                mostrarMensaje("No hay conexión a internet");
                return;
            }

            saveComments(comments, firebaseUserId, imageId, destinationId);
            saveRating(ratingValue, firebaseUserId, imageId, destinationId);

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
                registerIntent.putExtra("destinationId", getIntent().getStringExtra("destinationId"));
                registerIntent.putExtra("placeName", getIntent().getStringExtra("placeName"));
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

    void saveComments(String comments, String userFirebaseId, String imageId, String destinationId) {
        reference = FirebaseDatabase.getInstance().getReference();
        Comentarios comentario = new Comentarios(comments, userFirebaseId);

        reference.child("comments").child(destinationId).addListenerForSingleValueEvent(new ValueEventListener() {
            boolean isCommentUpdated = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comentarios comentarioExistente = dataSnapshot.getValue(Comentarios.class);
                    if (comentarioExistente != null && comentarioExistente.getId_user().equals(userFirebaseId)) {
                        comentarioExistente.setComment(comments);
                        reference.child("comments").child(destinationId)
                                .child(dataSnapshot.getKey()).setValue(comentarioExistente)
                                .addOnSuccessListener(aVoid -> mostrarMensaje("Comentario actualizado correctamente"))
                                .addOnFailureListener(e -> mostrarMensaje("Error al actualizar comentario"));
                        isCommentUpdated = true;
                        break;
                    }
                }

                if (!isCommentUpdated) {
                    reference.child("comments").child(destinationId).push().setValue(comentario)
                            .addOnSuccessListener(aVoid -> mostrarMensaje("Comentario guardado correctamente"))
                            .addOnFailureListener(e -> mostrarMensaje("Error al guardar comentario"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarMensaje("Error al guardar comentario");
            }
        });
    }


    void saveRating(float rating, String userFirebaseId, String imageId, String destinationId) {
        reference = FirebaseDatabase.getInstance().getReference();

        /*Ratings userRating = new Ratings(rating, userFirebaseId, imageId);*/
        Rating ratingUser = new Rating(userFirebaseId, rating);
        System.out.println("ImageID: " + imageId);

        getRatingFirebase(destinationId, userFirebaseId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Float ratingValue = task.getResult();
                Log.d("Tag", "valor retornado: " + ratingValue);
                if (ratingValue <= 5) {
                    mostrarMensaje("Ya has calificado este destino");
                    reference.child("ratings").child(destinationId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Rating ratingUser = dataSnapshot.getValue(Rating.class);
                                        ratingUser.setRating(rating);
                                        if (ratingUser.getId_user().equals(userFirebaseId)) {
                                            Log.d("Tag", "Key de rating: " + dataSnapshot.getKey());
                                            DatabaseReference referenceRating = FirebaseDatabase.getInstance().getReference();
                                            referenceRating.child("ratings").child(destinationId)
                                                    .child(dataSnapshot.getKey()).setValue(ratingUser)
                                                    .addOnSuccessListener(aVoid -> {
                                                        mostrarMensaje("Comentario guardado correctamente");
                                                    });
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    mostrarMensaje("Error al guardar comentario");
                                }
                            });
                } else {
                    reference.child("ratings").child(destinationId).push().setValue(ratingUser)
                            .addOnSuccessListener(aVoid -> {
                                mostrarMensaje("Comentario guardado correctamente");
                            })
                            .addOnFailureListener(e -> {
                                mostrarMensaje("Error al guardar comentario");
                            });
                }
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

    public Task<Float> getRatingFirebase(String destinationId, String userId) {
    TaskCompletionSource<Float> tcs = new TaskCompletionSource<>();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            float value = 6.0f;
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Rating rating = dataSnapshot.getValue(Rating.class);
                if (rating.getId_user().equals(userId)) {
                    value = rating.getRating();
                    break;
                }
            }
            tcs.setResult(value);
            reference.child("ratings").child(destinationId).removeEventListener(this);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            tcs.setException(error.toException());
        }
    };

    reference.child("ratings").child(destinationId).addValueEventListener(listener);

    return tcs.getTask();
}

}
