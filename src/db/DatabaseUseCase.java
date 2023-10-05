package db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUseCase {

    public static boolean isUsernameExists(String username) throws SQLException  {
        String query = "SELECT * FROM EMPLOYE WHERE USERNAME = '" + username + "'";

        ResultSet resultSet = DatabaseConnection.executeQuery(query);

        if(!resultSet.next())
        {
            return false;
        }

        return true;
    }



}
