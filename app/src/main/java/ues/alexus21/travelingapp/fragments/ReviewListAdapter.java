package ues.alexus21.travelingapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ues.alexus21.travelingapp.R;
import ues.alexus21.travelingapp.models.CommentRating;
import ues.alexus21.travelingapp.models.Usuario;

public class ReviewListAdapter extends BaseAdapter {

    private String destinationId;
    private String destinationName;
    private ArrayList<CommentRating> commentRatings;
    private Context context;

    public ReviewListAdapter(String destinationId, ArrayList<CommentRating> commentRatings, Context context, String destinationName) {
        this.destinationId = destinationId;
        this.commentRatings = commentRatings;
        this.context = context;
        this.destinationName = destinationName;
    }

    @Override
    public int getCount() {
        return commentRatings.size();
    }

    @Override
    public Object getItem(int position) {
        return commentRatings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_adapter_lista_reviews, null);

        TextView textViewPlace = convertView.findViewById(R.id.textViewPlace);
        TextView textViewUser = convertView.findViewById(R.id.textViewUser);
        RatingBar ratingBar2 = convertView.findViewById(R.id.ratingBar2);
        TextView textViewComment = convertView.findViewById(R.id.textViewComment);

        textViewPlace.setText(destinationName);

        textViewUser.setText("");
        ratingBar2.setRating(commentRatings.get(position).getRating().getRating());
        textViewComment.setText(commentRatings.get(position).getComentario().getComment());

        return convertView;
    }
}
