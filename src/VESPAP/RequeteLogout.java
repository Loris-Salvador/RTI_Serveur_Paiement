package VESPAP;

import hepl.be.ServeurGeneriqueTCP.Requete;

public class RequeteLogout implements Requete
{
    private String login;

    public RequeteLogout(String l) {
        login = l;
    }
    public String getLogin() {
        return login;
    }
}
