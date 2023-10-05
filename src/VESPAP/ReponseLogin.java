package VESPAP;

import hepl.be.ServeurGeneriqueTCP.Reponse;

public class ReponseLogin implements Reponse
{
    private boolean valide;
    private String message;

    ReponseLogin(boolean v, String m) {
        valide = v;
        message = m;
    }
    public boolean isValide() {
        return valide;
    }
    public String getMessage() {
        return message;
    }
}

