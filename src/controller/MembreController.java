package controller;

import dao.MembreDAO;
import model.Membre;
import java.time.LocalDate;
import java.util.List;

public class MembreController {

    private MembreDAO membreDAO = new MembreDAO();

    // Retourne tous les membres
    public List<Membre> getTousMembres() {
        return membreDAO.getTous();
    }

    // Ajoute un nouveau membre après vérification
    public void ajouterMembre(String login, String mdp, String nom, String prenom,LocalDate dateNaissance, String adresse,String telephone, String email, double poids) {

        // Vérifier que le login n'existe pas déjà
        if (membreDAO.trouverParLogin(login) != null)
            throw new IllegalArgumentException("Ce login est déjà utilisé.");

        int id = membreDAO.genererNouvelId();
        Membre nouveau = new Membre(id, login, mdp, nom, prenom, dateNaissance, adresse, telephone, email, poids);
        membreDAO.ajouter(nouveau);
    }

    // Modifie un membre existant
    public void modifierMembre(Membre membre) {
        if (membre.getNom().isBlank() || membre.getPrenom().isBlank())
            throw new IllegalArgumentException("Le nom et le prénom ne peuvent pas être vides.");

        membreDAO.modifier(membre);
    }

    // Supprime un membre par son id
    public void supprimerMembre(int id) {
        membreDAO.supprimer(id);
    }

    // Change le mot de passe d'un membre (utilisé au premier accès)
    public void changerMotDePasse(Membre membre, String nouveauMdp) {
        if (nouveauMdp.length() < 4)
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 4 caractères.");

        membre.setMotDePasse(nouveauMdp);
        membre.setPremierAcces(false); // ne plus demander le changement au prochain login
        membreDAO.modifier(membre);
    }
}