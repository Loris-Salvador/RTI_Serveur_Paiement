package VESPAPS;

import communication.Requete;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;

public class RequeteLoginNewEmploye implements Requete {

    private byte[]message; //crypté asymétriquement
    public RequeteLoginNewEmploye(String login, String password, PublicKey publicKey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(login);
        dos.writeUTF(password);

        byte[] messageClair = baos.toByteArray();

        message = MyCrypto.CryptAsymRSA(publicKey,messageClair);
    }

    public String getLogin(PrivateKey clePrivee) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] messageDecrypte = MyCrypto.DecryptAsymRSA(clePrivee, message);

        ByteArrayInputStream bais = new ByteArrayInputStream(messageDecrypte);
        DataInputStream dis = new DataInputStream(bais);
        return dis.readUTF();
    }
    public String getPassword(PrivateKey clePrivee) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, NoSuchProviderException, InvalidKeyException, IOException {
        byte[] messageDecrypte = MyCrypto.DecryptAsymRSA(clePrivee, message);

        ByteArrayInputStream bais = new ByteArrayInputStream(messageDecrypte);
        DataInputStream dis = new DataInputStream(bais);
        dis.readUTF();
        return dis.readUTF();
    }
}