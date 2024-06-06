package ues.alexus21.travelingapp.models;

public class CommentRating {

    private Comentarios comentario;
    private Rating rating;

    public CommentRating() {
    }

    public CommentRating(Comentarios comentario, Rating rating) {
        this.comentario = comentario;
        this.rating = rating;
    }

    public Comentarios getComentario() {
        return comentario;
    }

    public void setComentario(Comentarios comentario) {
        this.comentario = comentario;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Comentario: " + comentario.getComment() + " Rating: " + rating.getRating();
    }
}
