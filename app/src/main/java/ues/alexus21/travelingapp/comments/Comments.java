package ues.alexus21.travelingapp.comments;

public class Comments {
    public String comments;
    public String userId;
    public String destinationId;

    public Comments(String comments, String userId, String destinationId) {
        this.comments = comments;
        this.userId = userId;
        this.destinationId = destinationId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
