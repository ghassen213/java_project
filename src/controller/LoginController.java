package controller;

import dao.MembreDAO;
import model.Membre;

public class LoginController {

    // Identifiants admin codés en dur
    private String adminLogin = "admin";
    private String adminMdp   = "admin123";

    private MembreDAO membreDAO = new MembreDAO();

    // Vérifie si c'est l'admin
    public boolean estAdmin(String login, String mdp) {
        return adminLogin.equals(login) && adminMdp.equals(mdp);
    }

    // Retourne le membre si login/mdp sont corrects, sinon null
    public Membre connecterMembre(String login, String mdp) {
        Membre membre = membreDAO.trouverParLogin(login);

        if (membre == null) return null; // login introuvable

        if (membre.getMotDePasse().equals(mdp)) return membre; // mot de passe correct

        return null; // mot de passe incorrect
    }
}