package VESPAP;

import hepl.be.ServeurGeneriqueTCP.Reponse;
import hepl.be.model.Facture;

public class ReponseGetFactures implements Reponse
{
    private Facture[] tableauFactures;

    public ReponseGetFactures(Facture[] tabFactures)
    {
        tableauFactures = tabFactures;
    }

    public Facture[] getTableauFactures() {
        return tableauFactures;
    }
}
