package ues.alexus21.travelingapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ues.alexus21.travelingapp.DatabaseSingleton;
import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.adapters.ListaDestinosAdapter;
import ues.alexus21.travelingapp.localstorage.ILocalUserDAO;
import ues.alexus21.travelingapp.models.ListaDestinos;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritosFragment extends Fragment {

    private DatabaseReference reference;
    private ILocalUserDAO localUserDAO;
    private ArrayList<ListaDestinos> listaDestinos;
    private ListaDestinosAdapter listaDestinosAdapter;

    private ListView ltsDestinosTuristicos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoritosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritosFragment newInstance(String param1, String param2) {
        FavoritosFragment fragment = new FavoritosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        instanciaObjetos(view);

        String idUser = localUserDAO.getUserId();
        getFavorites(idUser);


        return view;
    }

    void instanciaObjetos(View root) {
        listaDestinos = new ArrayList<>();
        localUserDAO = DatabaseSingleton.getDatabase(getContext()).localUserDAO();
        reference = FirebaseDatabase.getInstance().getReference();
        ltsDestinosTuristicos = root.findViewById(R.id.ltsDestinosTuristicos);
    }

    void getFavorites(String idUser) {
        DatabaseReference referenceUser = reference.child("favorites").child(idUser);

        Log.d("FavoritosFragment", "ID Usuario2: " + idUser);
        referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaDestinos.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String idDestino = dataSnapshot.getKey();
                    Log.d("Id de destinos por favoritos", "ID Destino: " + idDestino);

                    DatabaseReference referenceDestination = reference.child("destination").child(idDestino);

                    referenceDestination.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ListaDestinos destino = snapshot.getValue(ListaDestinos.class);
                            listaDestinos.add(destino);
                            Log.d("FavoritosFragment", "Localización: " + destino);
                            Toast.makeText(getContext(), "Destino añadido a favoritos", Toast.LENGTH_SHORT).show();

                            listaDestinosAdapter = new ListaDestinosAdapter(listaDestinos, requireContext(), localUserDAO.getUserId());
                            ltsDestinosTuristicos.setAdapter(listaDestinosAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("FavoritosFragment", "Error al obtener los destinos por favoritos: " + error.getMessage());
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FavoritosFragment", "Error al obtener los favoritos: " + error.getMessage());
            }
        });
    }
}
