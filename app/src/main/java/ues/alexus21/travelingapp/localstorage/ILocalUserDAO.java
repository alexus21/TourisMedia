package ues.alexus21.travelingapp.localstorage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ILocalUserDAO {
    @Query("SELECT * FROM LocalUserModel WHERE email = :email AND password = :password")
    LocalUserModel getUser(String email, String password);

    @Query("SELECT * FROM LocalUserModel")
    List<LocalUserModel> getAll();

    // Obtener el ID
    @Query("SELECT user_remote_id FROM LocalUserModel WHERE isLogged = 1")
    String getUserId();

    // Obtener el ID
    @Query("SELECT user_remote_id FROM LocalUserModel WHERE email = :email")
    String checkIfExist(String email);

    // Obtener el ID
    @Query("SELECT email FROM LocalUserModel WHERE user_remote_id = :remoteId")
    String getEmailFromRemoteId(String remoteId);

    // Obtener el ID
    @Query("SELECT email FROM LocalUserModel")
    String checkIfExist();

    // Obtener el ID
    @Query("SELECT user_remote_id FROM LocalUserModel WHERE user_remote_id = :user_remote_id")
    String getUserId(String user_remote_id);

    @Insert
    void insertUser(LocalUserModel user);

    @Query("DELETE FROM LocalUserModel WHERE user_remote_id = :user_remote_id")
    void delete(String user_remote_id);

    @Query("UPDATE LocalUserModel SET isLogged = '1' WHERE email = :email")
    void updateUser(String email);

    @Query("UPDATE LocalUserModel SET isLogged = '0' WHERE user_remote_id = :user_remote_id")
    void logout(String user_remote_id);
}
