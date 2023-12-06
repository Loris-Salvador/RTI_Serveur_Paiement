package ServeurGeneriqueTCP;

import ServeurGeneriqueTCP.Exception.FinConnexionException;
import ServeurGeneriqueTCP.Logger.Logger;
import ServeurGeneriqueTCP.Protocole.Protocole;
import VESPAPS.ReponseLogin;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.concurrent.*;
import java.net.*;
public class ThreadServeurDemande extends ThreadServeur
{
    private ThreadPoolExecutor executor;
    private int nbThread;
    public ThreadServeurDemande(int port, Protocole protocole, Logger logger, int taillePool) throws
            IOException
    {
        super(port, protocole, logger);
        nbThread = taillePool;
        this.executor = new ThreadPoolExecutor(taillePool, taillePool,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());    }

    @Override
    public void run()
    {
        logger.Trace("Démarrage du TH Serveur (Demande)...");
        while(!this.isInterrupted())
        {
            Socket csocket = null;
            try {
                //ssocket.setSoTimeout(2000);

                logger.Trace("accept");
                csocket = ssocket.accept();

                logger.Trace("GetActive: "+executor.getActiveCount() + " nbthread: " + nbThread);
                if(executor.getActiveCount() == nbThread)
                {
                    ObjectOutputStream oos = new ObjectOutputStream(csocket.getOutputStream());
                    oos.writeObject(protocole.rejectRequete());
                    //csocket.close(); affiche pas le message
                    logger.Trace("Connexion refusée, Trop de TH Client");
                }
                else
                {
                    logger.Trace("Connexion acceptée, création TH Client");
                    executor.execute(new ThreadClientDemande(protocole, csocket, logger)); // Utilisez l'Executo
                }
            }
            catch (SocketTimeoutException ex)
            {
                // Pour vérifier si le thread a été interrompu
            }
            catch (IOException ex)
            {
                logger.Trace("Erreur I/O");
            } catch (FinConnexionException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (CertificateException e) {
                throw new RuntimeException(e);
            } catch (KeyStoreException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (NoSuchProviderException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        }
            logger.Trace("TH Serveur (Demande) interrompu.");
        try { ssocket.close(); }
        catch (IOException ex) { logger.Trace("Erreur I/O"); }
    }
}
