package dao;

import model.Membre;
import util.FichierUtil;
import java.util.List;

public class MembreDAO {

    private String fichier = "data/membres.dat";

    // Retourne tous les membres depuis le fichier
    public List<Membre> getTous() {
        return FichierUtil.lire(fichier);
    }

    // Ajoute un membre dans le fichier
    public void ajouter(Membre membre) {
        List<Membre> liste = getTous();
        liste.add(membre);
        FichierUtil.ecrire(fichier, liste);
    }

    // Remplace l'ancien membre par le nouveau (même id)
    public void modifier(Membre membreModifie) {
        List<Membre> liste = getTous();
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getId() == membreModifie.getId()) {
                liste.set(i, membreModifie);
                break;
            }
        }
        FichierUtil.ecrire(fichier, liste);
    }

    // Supprime le membre qui a cet id
    public void supprimer(int id) {
        List<Membre> liste = getTous();
        liste.removeIf(m -> m.getId() == id);
        FichierUtil.ecrire(fichier, liste);
    }

    // Cherche un membre par son login, retourne null si introuvable
    public Membre trouverParLogin(String login) {
        for (Membre m : getTous()) {
            if (m.getLogin().equals(login)) return m;
        }
        return null;
    }

    // Génère un nouvel id = le max existant + 1
    public int genererNouvelId() {
        int maxId = 0;
        for (Membre m : getTous()) {
            if (m.getId() > maxId) maxId = m.getId();
        }
        return maxId + 1;
    }
}