package db;

import model.Facture;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseUseCase {

    public synchronized boolean isUsernameExists(String username)  {
        String query = "SELECT * FROM EMPLOYE WHERE USERNAME = '" + username + "'";

        try {
            ResultSet resultSet = DatabaseConnection.executeQuery(query);

            if(!resultSet.next())
            {
                return false;
            }

            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.exit(1);
            return false;
        }

    }

    public synchronized boolean isLoginOk(String username, String password)  {
        String query = "SELECT * FROM EMPLOYE WHERE USERNAME = '" + username;

        try {


            ResultSet resultSet = DatabaseConnection.executeQuery(query);

            if (resultSet.next()) {
                String passwordDB = resultSet.getString("PASSWORD");

                if (passwordDB.equals(password)) {
                    return true;
                }
            }

            return false;
        }

        catch (SQLException e)
        {
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    public synchronized boolean addEmploye(String username, String password) {
        String query = "INSERT INTO EMPLOYE (USERNAME, PASSWORD) VALUES ('" + username + "', '" + password + "')";

        try
        {
            DatabaseConnection.executeUpdate(query);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public synchronized ArrayList<Facture> getFactures(int idClient) throws SQLException
    {
        ArrayList<Facture> factures = new ArrayList<>();

        String query = "SELECT * FROM FACTURE WHERE ID_CLIENT = " + idClient;

        ResultSet resultSet = DatabaseConnection.executeQuery(query);

        while(resultSet.next())
        {
            int idFacture = resultSet.getInt("ID_FACTURE");
            int idClientDB = resultSet.getInt("ID_CLIENT");
            Date date = resultSet.getDate("DATE_PAIEMENT");
            boolean etat = resultSet.getBoolean("PAYER");

            float montant = 0;


            String query2 = "SELECT * FROM ARTICLE_FACTURE WHERE ID_FACTURE = " + idFacture;

            ResultSet resultSet2 = DatabaseConnection.executeQuery(query2);

            while(resultSet2.next())
            {
                int quantite = resultSet2.getInt("QUANTITE");
                int idArticle = resultSet2.getInt("ID_ARTICLE");

                String query3 = "SELECT * FROM ARTICLE WHERE ID_ARTICLE = " + idArticle;

                ResultSet resultSet3 = DatabaseConnection.executeQuery(query3);

                float prix = 0;

                if(resultSet3.next())
                {
                    prix = resultSet3.getFloat("PRIX");
                }

                montant = montant + (prix * quantite);
            }

            Facture facture = new Facture(idFacture, date, montant, etat);

            factures.add(facture);

        }

        return factures;




    }


}
