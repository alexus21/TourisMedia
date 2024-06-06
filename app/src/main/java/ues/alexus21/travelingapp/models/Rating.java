package ues.alexus21.travelingapp.models;

public class Rating {
    private String id_user;
    private float rating;

    public Rating() {
    }

    public Rating(String id_user, float rating) {
        this.id_user = id_user;
        this.rating = rating;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
