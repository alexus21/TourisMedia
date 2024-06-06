package ues.alexus21.travelingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.models.ListaDestinos;

public class ListaFavoritosAdapter extends BaseAdapter {

    ArrayList<ListaDestinos> listaDestinos;
    Context context;
    TextView textViewPlaceName, textViewPlaceDescription, textViewPlaceLocation;
    ImageView imageViewFavouritePlaceMark;
    String idUsuario;

    public ListaFavoritosAdapter(ArrayList<ListaDestinos> listaDestinos, Context context, String idUsuario) {
        this.listaDestinos = listaDestinos;
        this.context = context;
        this.idUsuario = idUsuario;
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
        return 0;
    }

    @SuppressLint({"InflateParams", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_adapter_lista_destinos, null);
        }

        textViewPlaceName = convertView.findViewById(R.id.textViewPlaceName);
        textViewPlaceDescription = convertView.findViewById(R.id.textViewPlaceDescription);
        textViewPlaceLocation = convertView.findViewById(R.id.textViewPlaceLocation);
        imageViewFavouritePlaceMark = convertView.findViewById(R.id.imageViewFavouritePlaceMark);

        imageViewFavouritePlaceMark.setOnClickListener(v -> {
            // Eliminar de favoritos
        });

        return convertView;
    }
}
