package dao;

import model.Inscription;
import util.FichierUtil;
import java.util.ArrayList;
import java.util.List;

public class InscriptionDAO {

    private String fichier = "data/inscriptions.dat";

    // Retourne toutes les inscriptions depuis le fichier
    public List<Inscription> getTous() {
        return FichierUtil.lire(fichier);
    }

    // Ajoute une inscription dans le fichier
    public void ajouter(Inscription inscription) {
        List<Inscription> liste = getTous();
        liste.add(inscription);
        FichierUtil.ecrire(fichier, liste);
    }

    // Remplace l'ancienne inscription par la nouvelle (même id)
    public void modifier(Inscription inscriptionModifiee) {
        List<Inscription> liste = getTous();
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getId() == inscriptionModifiee.getId()) {
                liste.set(i, inscriptionModifiee);
                break;
            }
        }
        FichierUtil.ecrire(fichier, liste);
    }

    // Supprime l'inscription qui a cet id
    public void supprimer(int id) {
        List<Inscription> liste = getTous();
        liste.removeIf(i -> i.getId() == id);
        FichierUtil.ecrire(fichier, liste);
    }

    // Retourne toutes les inscriptions d'un membre
    public List<Inscription> getParMembre(int membreId) {
        List<Inscription> resultat = new ArrayList<>();
        for (Inscription i : getTous()) {
            if (i.getMembreId() == membreId) resultat.add(i);
        }
        return resultat;
    }

    // Retourne toutes les inscriptions d'une activité
    public List<Inscription> getParActivite(int activiteId) {
        List<Inscription> resultat = new ArrayList<>();
        for (Inscription i : getTous()) {
            if (i.getActiviteId() == activiteId) resultat.add(i);
        }
        return resultat;
    }

    // Compte les membres acceptés pour une activité
    public int getNombreParticipants(int activiteId) {
        int count = 0;
        for (Inscription i : getParActivite(activiteId)) {
            if (i.getStatut() == Inscription.Statut.ACCEPTEE) count++;
        }
        return count;
    }

    // Génère un nouvel id = le max existant + 1
    public int genererNouvelId() {
        int maxId = 0;
        for (Inscription i : getTous()) {
            if (i.getId() > maxId) maxId = i.getId();
        }
        return maxId + 1;
    }
}