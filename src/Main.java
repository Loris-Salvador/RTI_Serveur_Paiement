import db.DatabaseConnection;
import db.DatabaseUseCase;

import java.sql.*;

public class Main {
    public static void main(String[] args) {

        try {
            DatabaseConnection databaseConnection = new DatabaseConnection(DatabaseConnection.MYSQL, "localhost", "PSLA_RTI", "RTI", "RTI");
        }
        catch (SQLException e) {
            System.out.println("SQL Exception");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        try {
            System.out.println(DatabaseUseCase.isUsernameExists("Loris"));
        }
        catch (SQLException e) {
            System.out.println("SQL Exception" + e.getMessage());
        }


    }
}