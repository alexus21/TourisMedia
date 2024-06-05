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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.activities.PlaceReviewActivity;
import ues.alexus21.travelingapp.models.ListaDestinos;

public class ListaDestinosAdapter extends BaseAdapter {

    ArrayList<ListaDestinos> listaDestinos;
    Context context;
    SparseBooleanArray favouriteStatus;

    public ListaDestinosAdapter(ArrayList<ListaDestinos> listaDestinos, Context context) {
        this.listaDestinos = listaDestinos;
        this.context = context;
        this.favouriteStatus = new SparseBooleanArray();
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

    @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_adapter_lista_destinos, null);
        }

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textViewPlaceName = convertView.findViewById(R.id.textViewPlaceName);
        TextView textViewPlaceDescription = convertView.findViewById(R.id.textViewPlaceDescription);
        TextView textViewPlaceLocation = convertView.findViewById(R.id.textViewPlaceLocation);
        ImageView imageViewFavouritePlaceMark = convertView.findViewById(R.id.imageViewFavouritePlaceMark);

        ListaDestinos destino = listaDestinos.get(position);

        /*favouriteStatus.put(0, true);
        favouriteStatus.put(3, true);*/

        // Configura el icono inicial basado en el estado almacenado en SparseBooleanArray
        if (favouriteStatus.get(position, false)) {
            imageViewFavouritePlaceMark.setImageResource(R.drawable.icon_heart_relleno);
        } else {
            imageViewFavouritePlaceMark.setImageResource(R.drawable.icon_heart_contorno);
        }

        imageViewFavouritePlaceMark.setOnClickListener(v -> {
            boolean isFavourite = favouriteStatus.get(position, false);
            if (!isFavourite) {
                imageViewFavouritePlaceMark.setImageResource(R.drawable.icon_heart_relleno);
                favouriteStatus.put(position, true);
            } else {
                imageViewFavouritePlaceMark.setImageResource(R.drawable.icon_heart_contorno);
                favouriteStatus.put(position, false);
            }
        });

        Glide.with(context)
                .load(destino.getImg_url())
                .into(imageView);

        imageView.setOnClickListener(v -> {
            Intent placeReviewActivity = new Intent(context, PlaceReviewActivity.class);
            placeReviewActivity.putExtra("imageUrl", destino.getImg_url());
            context.startActivity(placeReviewActivity);
        });

        textViewPlaceName.setText(destino.getName());
        textViewPlaceDescription.setText("");
        textViewPlaceLocation.setText(destino.getLocation());

        return convertView;
    }
}
