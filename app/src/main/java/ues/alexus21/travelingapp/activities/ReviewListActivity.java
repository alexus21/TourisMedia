package ues.alexus21.travelingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.fragments.ReviewListAdapter;
import ues.alexus21.travelingapp.models.Comentarios;
import ues.alexus21.travelingapp.models.CommentRating;
import ues.alexus21.travelingapp.models.Rating;

public class ReviewListActivity extends AppCompatActivity {

    ImageView imgAtras;
    ArrayList<CommentRating> commentRatings;
    ReviewListAdapter reviewListAdapter;
    ListView listViewReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_list);

        imgAtras = findViewById(R.id.imgAtras);
        listViewReviews = findViewById(R.id.listViewReviews);

        imgAtras.setOnClickListener(v -> {
            Intent placeReviewIntent = new Intent(ReviewListActivity.this, HomeActivity.class);
            startActivity(placeReviewIntent);
            finish();
        });

        Intent intent = getIntent();
        String destinationId = intent.getStringExtra("destinationId");
        String destinationName = intent.getStringExtra("placeName");
        obtenerComentariosYCalificaciones(destinationId, destinationName);
    }

    void obtenerComentariosYCalificaciones(String destinationId, String destinationName) {
        DatabaseReference commentRatingRef = FirebaseDatabase.getInstance().getReference();
        commentRatings = new ArrayList<>();
        reviewListAdapter = new ReviewListAdapter(destinationId, commentRatings, this, destinationName);
        listViewReviews.setAdapter(reviewListAdapter);
        Log.d("Comment", "Obteniendo comentarios y calificaciones " + destinationName);

        commentRatingRef.child("comments").child(destinationId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentRatings.clear();
                        for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                            Comentarios comment = commentSnapshot.getValue(Comentarios.class);
                            if (comment == null) continue;

                            String idUsuario = comment.getId_user();
                            CommentRating commentRatingAux = new CommentRating();
                            commentRatingAux.setComentario(comment);
                            Log.d("Comment", "Encontró comentario: " + comment.getComment());

                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
                            userRef.child("ratings").child(destinationId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean ratingFound = false;
                                            for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                                                Rating rating = ratingSnapshot.getValue(Rating.class);
                                                if (rating != null && rating.getId_user().equals(idUsuario)) {
                                                    Log.d("Rating", "Encontró rating" + rating.getRating());
                                                    commentRatingAux.setRating(rating);
                                                    commentRatings.add(commentRatingAux);
                                                    ratingFound = true;
                                                    break;
                                                }
                                            }
                                            if (ratingFound) {
                                                reviewListAdapter.notifyDataSetChanged();
                                                Log.d("Comment", "" + commentRatingAux);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(ReviewListActivity.this, "Error al obtener calificaciones", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ReviewListActivity.this, "Error al obtener comentarios", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
