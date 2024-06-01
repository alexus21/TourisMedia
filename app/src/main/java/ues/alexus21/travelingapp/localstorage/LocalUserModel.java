package ues.alexus21.travelingapp.localstorage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class LocalUserModel {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo
    public String email;
    @ColumnInfo
    public String password;
    @ColumnInfo
    public int isLogged;
    @ColumnInfo
    public String user_remote_id;

    public LocalUserModel(String email, String password, int isLogged, String user_remote_id) {
        this.email = email;
        this.password = password;
        this.isLogged = isLogged;
        this.user_remote_id = user_remote_id;
    }

    public LocalUserModel() {
        // Constructor vacío
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_remote_id() {
        return user_remote_id;
    }

    public void setUser_remote_id(String user_remote_id) {
        this.user_remote_id = user_remote_id;
    }
}
