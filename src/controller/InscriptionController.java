package controller;

import dao.ActiviteDAO;
import dao.InscriptionDAO;
import model.Activite;
import model.Inscription;
import java.util.List;

public class InscriptionController {

    private InscriptionDAO inscriptionDAO = new InscriptionDAO();
    private ActiviteDAO activiteDAO = new ActiviteDAO();

    // Retourne toutes les inscriptions
    public List<Inscription> getToutesInscriptions() {
        return inscriptionDAO.getTous();
    }

    // Retourne les inscriptions d'un membre
    public List<Inscription> getInscriptionsMembre(int membreId) {
        return inscriptionDAO.getParMembre(membreId);
    }

    // Retourne les inscriptions d'une activité
    public List<Inscription> getInscriptionsActivite(int activiteId) {
        return inscriptionDAO.getParActivite(activiteId);
    }

    // Inscrit un membre à une activité
    public void inscrire(int membreId, int activiteId) {

        // 1. Vérifier si le membre est déjà inscrit
        for (Inscription i : inscriptionDAO.getParMembre(membreId)) {
            if (i.getActiviteId() == activiteId) {
                throw new IllegalArgumentException("Vous êtes déjà inscrit à cette activité.");
            }
        }

        // 2. Chercher l'activité
        Activite activite = null;
        for (Activite a : activiteDAO.getTous()) {
            if (a.getId() == activiteId) {
                activite = a;
                break;
            }
        }
        if (activite == null) {
            throw new IllegalArgumentException("Activité introuvable.");
        }

        // 3. Vérifier s'il reste des places
        int nbInscrits = inscriptionDAO.getNombreParticipants(activiteId);
        if (nbInscrits >= activite.getCapaciteMax()) {
            throw new IllegalArgumentException("Cette activité est complète.");
        }

        // 4. Créer et sauvegarder l'inscription
        int id = inscriptionDAO.genererNouvelId();
        inscriptionDAO.ajouter(new Inscription(id, membreId, activiteId));
    }

    // Annule (supprime) une inscription
    public void annuler(int inscriptionId) {
        inscriptionDAO.supprimer(inscriptionId);
    }

    // Valide une inscription
    public void valider(int inscriptionId) {
        changerStatut(inscriptionId, Inscription.Statut.ACCEPTEE);
    }

    // Refuse une inscription
    public void refuser(int inscriptionId) {
        changerStatut(inscriptionId, Inscription.Statut.REFUSEE);
    }

    // Change le statut d'une inscription (utilisé par valider et refuser)
    private void changerStatut(int inscriptionId, Inscription.Statut statut) {
        for (Inscription i : inscriptionDAO.getTous()) {
            if (i.getId() == inscriptionId) {
                i.setStatut(statut);
                inscriptionDAO.modifier(i);
                return;
            }
        }
        throw new IllegalArgumentException("Inscription introuvable.");
    }
}