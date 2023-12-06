package ServeurGeneriqueTCP.Protocole;
import ServeurGeneriqueTCP.Exception.FinConnexionException;
import communication.Reponse;
import communication.Requete;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

public interface Protocole
{
    String getNom();
    Reponse TraiteRequete(Requete requete) throws FinConnexionException, NoSuchAlgorithmException, IOException, NoSuchProviderException;
    Reponse rejectRequete() throws FinConnexionException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException;
}
