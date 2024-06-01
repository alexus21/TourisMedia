package com.ues.tourismedia;
public class destinos {
    String idDestino;
    String nombredestino;
    String descripciondestino;
    String ubicaciondestino;
    String imgdestinourl;
    String imgdestinoFirebaseurl;
    String token;

    public destinos(){}

    public destinos(String idDestino, String nombredestino, String descripciondestino, String ubicaciondestino,
                    String imgdestinourl, String imgdestinoFirebaseurl, String token) {

        this.idDestino = idDestino;
        this.nombredestino = nombredestino;
        this.descripciondestino = descripciondestino;
        this.ubicaciondestino = ubicaciondestino;
        this.imgdestinourl = imgdestinourl;
        this.imgdestinoFirebaseurl = imgdestinoFirebaseurl;
        this.token = token;

    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}