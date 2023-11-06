package VESPAPS;

import communication.Requete;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class RequetePayFacture implements Requete {
    private byte[] messageCrypte;

    public RequetePayFacture(int id, String n, int numCarte, SecretKey cleSession) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream oos = new DataOutputStream(baos);
        oos.writeInt(id);
        oos.writeUTF(n);
        oos.writeInt(numCarte);

        byte[] messageClair = baos.toByteArray();

        messageCrypte = MyCrypto.CryptSymDES(cleSession,messageClair);

    }

    public int getIdFacture(SecretKey cleSession) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        byte[] messageClair = MyCrypto.DecryptSymDES(cleSession, messageCrypte);

        ByteArrayInputStream bais = new ByteArrayInputStream(messageClair);
        DataInputStream ois = new DataInputStream(bais);

        int id = ois.readInt();

        return id;
    }

    public String getNom(SecretKey cleSession) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        byte[] messageClair = MyCrypto.DecryptSymDES(cleSession, messageCrypte);

        ByteArrayInputStream bais = new ByteArrayInputStream(messageClair);
        DataInputStream ois = new DataInputStream(bais);

        ois.readInt();
        String n = ois.readUTF();
        return n;
    }

    public int getNumeroCarte(SecretKey cleSession) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        byte[] messageClair = MyCrypto.DecryptSymDES(cleSession, messageCrypte);

        ByteArrayInputStream bais = new ByteArrayInputStream(messageClair);
        DataInputStream ois = new DataInputStream(bais);

        ois.readInt();
        ois.readUTF();
        int num = ois.readInt();

        return num;
    }
}
