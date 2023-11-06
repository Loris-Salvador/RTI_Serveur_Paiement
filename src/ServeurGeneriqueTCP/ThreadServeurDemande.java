package ServeurGeneriqueTCP;

import ServeurGeneriqueTCP.Logger.Logger;
import ServeurGeneriqueTCP.Protocole.Protocole;
import java.io.IOException;
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
            Socket csocket;
            try {
                //ssocket.setSoTimeout(2000);

                logger.Trace("accept");
                csocket = ssocket.accept();

                logger.Trace("GetActive: "+executor.getActiveCount() + " nbthread: " + nbThread);
                if(executor.getActiveCount() == nbThread)
                {
                    ssocket.close();
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
            }
        }
            logger.Trace("TH Serveur (Demande) interrompu.");
        try { ssocket.close(); }
        catch (IOException ex) { logger.Trace("Erreur I/O"); }
    }
}
