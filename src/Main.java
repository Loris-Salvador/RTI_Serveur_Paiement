import ServeurGeneriqueTCP.Logger.MonLogger;
import ServeurGeneriqueTCP.ThreadServeurPool;
import ServeurGeneriqueTCP.Protocole.VESPAP;
import db.DatabaseConnection;
import db.DatabaseUseCase;

import java.sql.*;

public class Main {
    public static void main(String[] args) {

        try {
            DatabaseConnection databaseConnection = new DatabaseConnection(DatabaseConnection.MYSQL, "localhost", "PSLA_RTI", "RTI", "RTI");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        MonLogger logger = new MonLogger();

        DatabaseUseCase databaseUseCase = new DatabaseUseCase(logger);
        VESPAP vespap = new VESPAP(logger,databaseUseCase);

        try {
            ThreadServeurPool threadServeurPool = new ThreadServeurPool(6666, vespap, 5, logger);
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