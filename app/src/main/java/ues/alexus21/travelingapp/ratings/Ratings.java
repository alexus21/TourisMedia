package ues.alexus21.travelingapp.ratings;

public class Ratings {
    float rating;
    public String userId;
    public String destinationId;

    public Ratings(float rating, String userId, String destinationId) {
        this.rating = rating;
        this.userId = userId;
        this.destinationId = destinationId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }
}
