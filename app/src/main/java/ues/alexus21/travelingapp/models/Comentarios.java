package ues.alexus21.travelingapp.models;

public class Comentarios {
    private String comment;
    private String id_user;

    public Comentarios() {
    }

    public Comentarios(String comment, String id_user) {
        this.comment = comment;
        this.id_user = id_user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
