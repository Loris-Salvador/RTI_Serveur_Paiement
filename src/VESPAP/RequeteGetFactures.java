package VESPAP;

import communication.Requete;

public class RequeteGetFactures implements Requete {
    private int idClient;

    public RequeteGetFactures(int id)
    {
        idClient = id;
    }

    public int getIdClient() {
        return idClient;
    }
}
