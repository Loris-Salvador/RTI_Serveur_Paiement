package ServeurGeneriqueTCP.Protocole;

import ServeurGeneriqueTCP.Exception.FinConnexionException;
import ServeurGeneriqueTCP.Logger.Logger;
import communication.Reponse;
import communication.Requete;
import db.DatabaseUseCase;
import model.Article;
import model.Facture;
import VESPAPS.*;

import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class VESPAPS implements Protocole {

    private Logger logger;
    private DatabaseUseCase databaseUseCase;
    private SecretKey cleSession;

    public VESPAPS(Logger log, DatabaseUseCase databaseUseCase)
    {
        logger = log;
        this.databaseUseCase = databaseUseCase;
    }


    @Override
    public String getNom() {
        return "VESPAPS";
    }

    @Override
    public synchronized Reponse TraiteRequete(Requete requete) throws FinConnexionException, NoSuchAlgorithmException, IOException, NoSuchProviderException {

        logger.Trace("Traitement requete");

        if(requete instanceof RequeteLogin)
            return TraiteRequeteLogin((RequeteLogin)requete);
        else if (requete instanceof RequeteLoginNewEmploye)
            return TraiteRequeteLoginNewEmploye((RequeteLoginNewEmploye)requete);
        else if(requete instanceof RequeteGetFactures)
            return TraiteRequeteGetFactures((RequeteGetFactures)requete);
        else if(requete instanceof RequetePayFacture)
            return TraiteRequetePayFacture((RequetePayFacture)requete);
        else if(requete instanceof RequeteLogout)
            TraiteRequeteLogout((RequeteLogout)requete);
        else if(requete instanceof RequeteGetArticles)
            return TraiteRequeteGetArticle((RequeteGetArticles)requete);

        return null;
    }

    @Override
    public synchronized Reponse rejectRequete() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {


        return new ReponseLogin("Trop de clients connectes", false, null, RecupereClePubliqueClient());
    }


    private synchronized Reponse TraiteRequeteLogin(RequeteLogin requeteLogin) throws FinConnexionException {
        logger.Trace("RequeteLOGIN reçue de " + requeteLogin.getLogin());

        try {
            boolean valide = false;
            String message = "Pas Normal";
            String motDePasse = "";
            motDePasse = databaseUseCase.getPassword(requeteLogin.getLogin());

            if(motDePasse.equals("")) {
                message = "Utilisateur inexistant";
            }
            else if(requeteLogin.VerifyPassword(motDePasse)) {
                message = "Login OK";
                valide = true;
            }
            else{
                message = "Mauvais mot de passe";
            }

            if(valide)
            {
                logger.Trace("OK:ReponseLogin envois");

                KeyGenerator cleGen = KeyGenerator.getInstance("DES","BC");
                cleGen.init(new SecureRandom());
                cleSession = cleGen.generateKey();

                ReponseLogin reponseLogin = new ReponseLogin(message, valide, cleSession, RecupereClePubliqueClient());
                return reponseLogin;
            }


            ReponseLogin reponseLogin = new ReponseLogin(message, valide, null, RecupereClePubliqueClient());
            throw new FinConnexionException(reponseLogin);

        } catch (NoSuchAlgorithmException | IOException | NoSuchProviderException | NoSuchPaddingException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException | KeyStoreException |
                 CertificateException e) {
            logger.Trace("Server: Error Login() "+e);
            throw new FinConnexionException(new ReponseLogin());
        }
    }

    private synchronized Reponse TraiteRequeteLoginNewEmploye(RequeteLoginNewEmploye requeteLoginNewEmploye) throws FinConnexionException {
        try {
            logger.Trace("RequeteLOGIN_NewEmploye reçue de " + requeteLoginNewEmploye.getLogin(RecupereClePriveeServeur()));

            String username = requeteLoginNewEmploye.getLogin(RecupereClePriveeServeur());
            String password = requeteLoginNewEmploye.getPassword(RecupereClePriveeServeur());

            if(!databaseUseCase.isUsernameExists(username))
            {
                logger.Trace("OK:ReponseLogin envois");

                databaseUseCase.addEmploye(username, password);

                // Création de la clé de session
                KeyGenerator cleGen = KeyGenerator.getInstance("DES","BC");
                cleGen.init(new SecureRandom());
                cleSession = cleGen.generateKey();

                ReponseLogin reponseLogin = new ReponseLogin("Employe cree", true, cleSession, RecupereClePubliqueClient());
                return reponseLogin;
            }
            else
            {
                throw new FinConnexionException(new ReponseLogin("Nom de Client deja existant", false, null, RecupereClePubliqueClient()));
            }

        } catch (NoSuchPaddingException | IllegalBlockSizeException | KeyStoreException | IOException |
                 UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | BadPaddingException |
                 NoSuchProviderException | InvalidKeyException e) {
            logger.Trace("Server: Error LoginNewEmploye() "+e);
            throw new FinConnexionException(new ReponseLogin());
        }
    }

    private synchronized Reponse TraiteRequeteGetFactures(RequeteGetFactures requete) {
        logger.Trace("RequeteGETFACTURES reçue de " + requete.getIdClient());

        int clientId = requete.getIdClient();
        try {
            PublicKey clePublique = RecupereClePubliqueClient();

            if (!requete.VerifySignature(clePublique))
                return new ReponseGetFactures(new ArrayList<>(), "Mauvaise signature", cleSession);

            ArrayList<Facture> factures = databaseUseCase.getFactures(clientId);
            ReponseGetFactures reponse = new ReponseGetFactures(factures, "OK", cleSession);
            return reponse;
        }
        catch (Exception e)
        {
            logger.Trace("Server: Error TraiteRequeteGetFactures() "+e);
        }
        return null;
    }

    private synchronized Reponse TraiteRequetePayFacture(RequetePayFacture requete)
    {
        try {
            logger.Trace("RequetePAYFACTURE reçue de " + requete.getNom(cleSession));
            String message = databaseUseCase.payFacture(requete.getIdFacture(cleSession), requete.getNom(cleSession), requete.getNumeroCarte(cleSession));
            if(message.equals("OK"))
                return new ReponsePayFacture(true, "PAYER", cleSession);
            else
                return new ReponsePayFacture(false, message, cleSession);
        }
        catch (Exception e)
        {
            try {
                return new ReponsePayFacture(false, "Probleme BD", cleSession);
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | IOException ex) {
                logger.Trace("Error: TraiteRequetePayFacture "+e);
            }
        }

        return null;
    }

    private synchronized void TraiteRequeteLogout(RequeteLogout requete) throws FinConnexionException
    {
        logger.Trace("RequeteLOGOUT reçue de " + requete.getLogin());
        logger.Trace(requete.getLogin() + " correctement déloggé");
        throw new FinConnexionException(null);
    }

    private synchronized Reponse TraiteRequeteGetArticle(RequeteGetArticles requete)
    {
        logger.Trace("RequeteGETARTICLES reçue de " + requete.getIdFacture());

        int factureId = requete.getIdFacture();
        try {
            PublicKey clePublique = RecupereClePubliqueClient();

            if (!requete.VerifySignature(clePublique))
                return new ReponseGetArticles(new ArrayList<>(), "Mauvaise signature", cleSession);

            ArrayList<Article> articles = databaseUseCase.getArticles(factureId);
            ReponseGetArticles reponse = new ReponseGetArticles(articles, "OK",cleSession);
            return reponse;
        }
        catch (Exception e)
        {
            logger.Trace("Server: Error TraiteRequeteGetArticles() "+e);
        }
        return null;
    }

    public static PublicKey RecupereClePubliqueClient() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("KeystoreServeur.jks"),"serveur".toCharArray());
        X509Certificate certif = (X509Certificate)ks.getCertificate("Client");
        PublicKey cle = certif.getPublicKey();
        return cle;
    }

    public static PrivateKey RecupereClePriveeServeur() throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("KeystoreServeur.jks"),"serveur".toCharArray());
        PrivateKey cle = (PrivateKey) ks.getKey("Serveur","serveur".toCharArray());
        return cle;
    }
}
