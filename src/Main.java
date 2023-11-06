import ServeurGeneriqueTCP.Logger.MonLogger;
import ServeurGeneriqueTCP.ThreadServeurDemande;
import ServeurGeneriqueTCP.ThreadServeurPool;
import db.DatabaseConnection;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import db.*;
import ServeurGeneriqueTCP.Protocole.*;
import ServeurGeneriqueTCP.Protocole.VESPAPS;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Security;
import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {

        //DB////////////////////////////////////////////////////
        try {
            DatabaseConnection databaseConnection = new DatabaseConnection(DatabaseConnection.MYSQL, "localhost", "PSLA_RTI", "RTI", "RTI");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        //VESPAP///////////////////////////////////////////////////////

        MonLogger logger1 = new MonLogger();

        Security.addProvider(new BouncyCastleProvider());
        DatabaseUseCase databaseUseCase = new DatabaseUseCase(logger1);
        VESPAP vespap = new VESPAP(logger1,databaseUseCase);

        Properties properties1 = new Properties();

        try (FileInputStream fis = new FileInputStream("config_non_securise.properties")) {
            properties1.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ThreadServeurPool threadServeurPool = new ThreadServeurPool(Integer.parseInt(properties1.getProperty("port")), vespap, Integer.parseInt(properties1.getProperty("taille_pool")), logger1);
            threadServeurPool.start();
        }
        catch (Exception e)
        {
            System.out.println("Erreur lors du lancement du serveur");
            e.printStackTrace();
            System.exit(1);
        }

        ///VESPAPS////////////////////////////////////////////////////

        MonLogger logger2 = new MonLogger();

        Security.addProvider(new BouncyCastleProvider());
        VESPAPS vespaps = new VESPAPS(logger2,databaseUseCase);

        Properties properties2 = new Properties();

        try (FileInputStream fis = new FileInputStream("config_securise.properties")) {
            properties2.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


        try {
            ThreadServeurDemande threadServeurPool = new ThreadServeurDemande(Integer.parseInt(properties2.getProperty("port")), vespaps, logger2, Integer.parseInt(properties2.getProperty("taille_pool")));
            threadServeurPool.start();
        }
        catch (Exception e)
        {
            System.out.println("Erreur lors du lancement du serveur");
            e.printStackTrace();
            System.exit(1);
        }

    }
}