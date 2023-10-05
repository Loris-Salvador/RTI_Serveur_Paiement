package ServeurGeneriqueTCP;
import VESPAP.Reponse;
import VESPAP.Requete;
import db.DatabaseUseCase;

import java.net.Socket;
public interface Protocole
{
    String getNom();
    Reponse TraiteRequete(Requete requete, DatabaseUseCase databaseUseCase) throws FinConnexionException;
}
