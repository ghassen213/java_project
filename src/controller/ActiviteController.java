package controller;

import dao.ActiviteDAO;
import dao.InscriptionDAO;
import model.Activite;
import java.util.List;
import java.util.ArrayList;

public class ActiviteController {

    private ActiviteDAO activiteDAO = new ActiviteDAO();
    private InscriptionDAO inscriptionDAO = new InscriptionDAO();

    // Retourne toutes les activités
    public List<Activite> getToutesActivites() {
        return activiteDAO.getTous();
    }

    // Ajoute une nouvelle activité après vérification
    public void ajouterActivite(String nom, String description, int capaciteMax, String horaire) {
        if (nom.isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (capaciteMax <= 0)
            throw new IllegalArgumentException("La capacité doit être supérieure à 0.");

        int id = activiteDAO.genererNouvelId();
        activiteDAO.ajouter(new Activite(id, nom, description, capaciteMax, horaire));
    }

    // Modifie une activité existante
    public void modifierActivite(Activite activite) {
        activiteDAO.modifier(activite);
    }

    // Supprime une activité par son id
    public void supprimerActivite(int id) {
        activiteDAO.supprimer(id);
    }

    // Calcule les places restantes d'une activité
    public int getPlacesRestantes(int activiteId) {
        for (Activite a : activiteDAO.getTous()) {
            if (a.getId() == activiteId) {
                int inscrits = inscriptionDAO.getNombreParticipants(activiteId);
                return a.getCapaciteMax() - inscrits;
            }
        }
        return 0; // activité introuvable
    }

    // Retourne uniquement les activités dont toutes les places sont prises
    public List<Activite> getActivitesCompletes() {
        List<Activite> completes = new ArrayList<>();
        for (Activite a : activiteDAO.getTous()) {
            int inscrits = inscriptionDAO.getNombreParticipants(a.getId());
            if (inscrits >= a.getCapaciteMax()) {
                completes.add(a);
            }
        }
        return completes;
    }
}