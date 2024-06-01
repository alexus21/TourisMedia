package ues.alexus21.travelingapp.localstorage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ILocalUserDAO {
    @Query("SELECT * FROM LocalUserModel WHERE email = :email AND password = :password")
    LocalUserModel getUser(String email, String password);

    // Obtener el ID
    @Query("SELECT user_remote_id FROM LocalUserModel WHERE isLogged = 1")
    String getUserId();

    @Insert
    void insertUser(LocalUserModel user);

    @Query("DELETE FROM LocalUserModel")
    void deleteAll();

    @Query("UPDATE LocalUserModel SET email = :email, password = :password WHERE id = :id")
    void updateUser(String id, String email, String password);
}
