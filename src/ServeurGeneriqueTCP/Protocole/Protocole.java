package ServeurGeneriqueTCP.Protocole;
import ServeurGeneriqueTCP.Exception.FinConnexionException;
import VESPAP.Reponse;
import VESPAP.Requete;

public interface Protocole
{
    String getNom();
    Reponse TraiteRequete(Requete requete) throws FinConnexionException;
}