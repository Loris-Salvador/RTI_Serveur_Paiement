package VESPAP;

import ServeurGeneriqueTCP.*;
import db.DatabaseConnection;
import db.DatabaseUseCase;
import model.Facture;

import java.net.Socket;
import java.util.ArrayList;

public class VESPAP implements Protocole {

    private Logger logger;
    private DatabaseUseCase databaseUseCase;

    public VESPAP(Logger log, DatabaseUseCase databaseUseCase)
    {
        logger = log;
        this.databaseUseCase = databaseUseCase;
    }


    @Override
    public String getNom() {
        return "VESPAP";
    }

    @Override
    public synchronized Reponse TraiteRequete(Requete requete, DatabaseUseCase databaseUseCase) throws FinConnexionException {

        if(requete instanceof RequeteLogin)
            return TraiteRequeteLogin((RequeteLogin)requete);
        else if(requete instanceof RequeteGetFactures)
            return TraiteRequeteGetFactures((RequeteGetFactures)requete);
       else if(requete instanceof RequetePayFacture)
//            return TraiteRequetePayFacture((RequetePayFacture)requete);
        if(requete instanceof RequeteLogout)
            TraiteRequeteLogout((RequeteLogout)requete);
        return null;
    }


    private synchronized Reponse TraiteRequeteLogin(RequeteLogin requeteLogin) throws FinConnexionException
    {
        ReponseLogin reponseLogin = new ReponseLogin();

        boolean isNewClient = requeteLogin.isNewEmploye();
        String username = requeteLogin.getLogin();
        String password = requeteLogin.getPassword();

        if(!isNewClient)
        {
            if(databaseUseCase.isLoginOk(username, password))
            {
                reponseLogin.setMessage("Login OK");
                reponseLogin.setValide(true);
                return reponseLogin;
            }
            else
            {
                reponseLogin.setValide(false);

                if(databaseUseCase.isUsernameExists(username))
                {
                    reponseLogin.setMessage("Mauvais mot de passe");
                    throw new FinConnexionException(new ReponseLogin(false, "Mauvais"));

                }
                else
                {
                    reponseLogin.setMessage("Utilisateur inexistant");
                    throw new FinConnexionException(reponseLogin);
                }
            }
        }
        else if(!databaseUseCase.isUsernameExists(username))
        {
            databaseUseCase.addEmploye(username, password);
            reponseLogin.setMessage("Utilisateur créé");
            reponseLogin.setValide(true);
            return reponseLogin;
        }
        else
        {
            throw new FinConnexionException(new ReponseLogin(false, "Nom de Client deja existant"));
        }
    }



    private synchronized Reponse TraiteRequeteGetFactures(RequeteGetFactures requete)
    {
        try {
            ArrayList<Facture> factures = databaseUseCase.getFactures(requete.getIdClient());
            ReponseGetFactures reponse = new ReponseGetFactures(factures, "OK");
            return reponse;
        }
        catch (Exception e)
        {
            return new ReponseGetFactures(new ArrayList<>(), "Probleme BD");
        }


    }

    private synchronized void TraiteRequetePayFacture(RequetePayFacture requete)
    {

    }

    private synchronized void TraiteRequeteLogout(RequeteLogout requete) throws FinConnexionException
    {
        logger.Trace("RequeteLOGOUT reçue de " + requete.getLogin());
        logger.Trace(requete.getLogin() + " correctement déloggé");
        throw new FinConnexionException(null);
    }
}
