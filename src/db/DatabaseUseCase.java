package db;

import ServeurGeneriqueTCP.Logger.Logger;
import model.Article;
import model.Facture;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseUseCase {

    private Logger logger;

    public DatabaseUseCase(Logger logger)
    {
        this.logger = logger;
    }

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
        String query = "SELECT * FROM EMPLOYE WHERE USERNAME = '" + username + "'";

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

    public synchronized String getPassword(String username)  {
        String query = "SELECT * FROM EMPLOYE WHERE USERNAME = '" + username + "'";

        try {
            ResultSet resultSet = DatabaseConnection.executeQuery(query);

            if (resultSet.next()) {
                String passwordDB = resultSet.getString("PASSWORD");

                return passwordDB;
            }

            return "";
        }

        catch (SQLException e)
        {
            e.printStackTrace();
            System.exit(1);
            return "";
        }
    }

    public synchronized boolean addEmploye(String username, String password) {

        System.out.println("addEmploye");

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

        String query = "SELECT * FROM FACTURE WHERE ID_CLIENT = " + idClient + " AND DATE_PAIEMENT IS NOT NULL";

        ResultSet resultSet = DatabaseConnection.executeQuery(query);

        while(resultSet.next())
        {
            logger.Trace("Facture trouvée");
            int idFacture = resultSet.getInt("ID");
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

                String query3 = "SELECT * FROM ARTICLE WHERE ID = " + idArticle;

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



    public synchronized String payFacture(int idFacture, String nom, int numeroCarte) throws SQLException
    {
        String query = "SELECT * FROM CARTE_BANCAIRE WHERE NUMERO_CARTE = " + numeroCarte;

        ResultSet resultSet = DatabaseConnection.executeQuery(query);

        if(!resultSet.next())
        {
            return "Carte bancaire invalide";
        }

        int idClient = resultSet.getInt("ID_CLIENT");

        String query2 = "SELECT * FROM CLIENT WHERE ID = " + idClient;

        ResultSet resultSet2 = DatabaseConnection.executeQuery(query2);

        if(!resultSet2.next())
        {
            return "jamais arrive";
        }

        String nomDB = resultSet2.getString("USERNAME");

        if(!nomDB.equals(nom))
        {
            return "Nom invalide";
        }

        String query3 = "UPDATE FACTURE SET PAYER = TRUE WHERE ID = " + idFacture;

        DatabaseConnection.executeUpdate(query3);

        return "OK";
    }

    public synchronized ArrayList<Article> getArticles(int idFacture)
    {
        String query = "SELECT * FROM ARTICLE_FACTURE WHERE ID_FACTURE = " + idFacture;

        ArrayList<Article> articles = new ArrayList<>();

        try {
            ResultSet resultSet = DatabaseConnection.executeQuery(query);

            while(resultSet.next())
            {
                int idArticle = resultSet.getInt("ID_ARTICLE");
                String query2 = "SELECT * FROM ARTICLE WHERE ID = " + idArticle;
                int quantite = resultSet.getInt("QUANTITE");

                ResultSet resultSet2 = DatabaseConnection.executeQuery(query2);

                resultSet2.next();
                Article article = new Article(resultSet2.getString("INTITULE"), quantite, resultSet2.getFloat("PRIX"));

                articles.add(article);

            }

            return articles;


        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
