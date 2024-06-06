package ues.alexus21.travelingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.activities.PlaceReviewActivity;
import ues.alexus21.travelingapp.firebasedatacollection.FirebaseDataCollection;
import ues.alexus21.travelingapp.models.ListaDestinos;

public class ListaDestinosAdapter extends BaseAdapter {

    ArrayList<ListaDestinos> listaDestinos;
    Context context;
    SparseBooleanArray favouriteStatus;
    String idUsuario;

    public ListaDestinosAdapter(ArrayList<ListaDestinos> listaDestinos, Context context, String idUsuario) {
        this.listaDestinos = listaDestinos;
        this.context = context;
        this.favouriteStatus = new SparseBooleanArray();
        this.idUsuario = idUsuario;
        checkFavourites();
    }

    @Override
    public int getCount() {
        return listaDestinos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaDestinos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_adapter_lista_destinos, null);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textViewPlaceName = convertView.findViewById(R.id.textViewPlaceName);
        TextView textViewPublishedBy = convertView.findViewById(R.id.textViewPublishedBy);
        ImageView imageViewFavouritePlaceMark = convertView.findViewById(R.id.imageViewFavouritePlaceMark);

        ListaDestinos destino = listaDestinos.get(position);

        // Configura el icono inicial basado en el estado almacenado en SparseBooleanArray
        if (favouriteStatus.get(position, false)) {
            imageViewFavouritePlaceMark.setImageResource(R.drawable.icon_heart_relleno);
        } else {
            imageViewFavouritePlaceMark.setImageResource(R.drawable.icon_heart_contorno);
        }

        imageViewFavouritePlaceMark.setOnClickListener(v -> {
            boolean isFavourite = favouriteStatus.get(position, false);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("favorites").child(idUsuario).child(destino.getId());

            if (!isFavourite) {
                imageViewFavouritePlaceMark.setImageResource(R.drawable.icon_heart_relleno);
                favouriteStatus.put(position, true);

                reference.setValue(true).addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Destino añadido a favoritos", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al añadir destino a favoritos", Toast.LENGTH_SHORT).show();
                });

            } else {
                imageViewFavouritePlaceMark.setImageResource(R.drawable.icon_heart_contorno);
                favouriteStatus.put(position, false);

                reference.removeValue().addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Destino eliminado de favoritos", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar destino de favoritos", Toast.LENGTH_SHORT).show();
                });
            }
        });

        Glide.with(context)
                .load(destino.getImg_url())
                .into(imageView);

        FirebaseDataCollection.getEmailFromDestinationId(destino.getId(), email -> {
            if (email != null) {
                textViewPublishedBy.setText("Publicado por: " + email.split("@")[0]);
            } else {
                textViewPublishedBy.setText("Publicación anónima");
            }
        });

        textViewPlaceName.setText(destino.getName() + ", " + destino.getLocation());
//        textViewPublishedBy.setText("Anónimo");

        imageView.setOnClickListener(v -> {

            FirebaseDataCollection.getEmailFromDestinationId(destino.getId(), email -> {
                if (email != null) {
                    String publishedBy = email.split("@")[0];
                    textViewPublishedBy.setText("Publicado por: " + publishedBy);

                    // Pasar el email a la siguiente actividad
                    Intent placeReviewActivity = new Intent(context, PlaceReviewActivity.class);
                    placeReviewActivity.putExtra("publisherBy", publishedBy);

                    // Agregar otros extras si es necesario
                    placeReviewActivity.putExtra("imageUrl", destino.getImg_url());
                    placeReviewActivity.putExtra("placeName", destino.getName());
                    placeReviewActivity.putExtra("placeLocation", destino.getLocation());
                    placeReviewActivity.putExtra("placeDescription", destino.getDescription());
                    placeReviewActivity.putExtra("placeId", destino.getId());

                    context.startActivity(placeReviewActivity);
                } else {
                    textViewPublishedBy.setText("Publicación anónima");
                }
            });
        });

        return convertView;
    }

    private void checkFavourites() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("favorites").child(idUsuario);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (int i = 0; i < listaDestinos.size(); i++) {
                    ListaDestinos destino = listaDestinos.get(i);
                    favouriteStatus.put(i, dataSnapshot.hasChild(destino.getId()));
                }
                notifyDataSetChanged(); // Actualiza la vista después de verificar favoritos
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja el error
                Toast.makeText(context, "Error al verificar favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
