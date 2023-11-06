package ServeurGeneriqueTCP.Protocole;
import ServeurGeneriqueTCP.Exception.FinConnexionException;
import communication.Reponse;
import communication.Requete;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface Protocole
{
    String getNom();
    Reponse TraiteRequete(Requete requete) throws FinConnexionException, NoSuchAlgorithmException, IOException, NoSuchProviderException;
}
