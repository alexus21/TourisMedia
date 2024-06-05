package ues.alexus21.travelingapp.models;

public class ListaDestinos {
    public String description;
    public String img_url;
    public String location;
    public String name;

    public ListaDestinos() {
    }

    public ListaDestinos(String description, String location, String name) {
        this.description = description;
        this.location = location;
        this.name = name;
    }

    public ListaDestinos(String description, String img_url, String location, String name) {
        this.description = description;
        this.img_url = img_url;
        this.location = location;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}