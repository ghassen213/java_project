package dao;

import model.Activite;
import util.FichierUtil;
import java.util.List;

public class ActiviteDAO {

    private String fichier = "data/activites.dat";

    // Retourne toutes les activités depuis le fichier
    public List<Activite> getTous() {
        return FichierUtil.lire(fichier);
    }

    // Ajoute une activité dans le fichier
    public void ajouter(Activite activite) {
        List<Activite> liste = getTous();
        liste.add(activite);
        FichierUtil.ecrire(fichier, liste);
    }

    // Remplace l'ancienne activité par la nouvelle (même id)
    public void modifier(Activite activiteModifiee) {
        List<Activite> liste = getTous();
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getId() == activiteModifiee.getId()) {
                liste.set(i, activiteModifiee);
                break;
            }
        }
        FichierUtil.ecrire(fichier, liste);
    }

    // Supprime l'activité qui a cet id
    public void supprimer(int id) {
        List<Activite> liste = getTous();
        liste.removeIf(a -> a.getId() == id);
        FichierUtil.ecrire(fichier, liste);
    }

    // Génère un nouvel id = le max existant + 1
    public int genererNouvelId() {
        int maxId = 0;
        for (Activite a : getTous()) {
            if (a.getId() > maxId) 
                maxId = a.getId();
        }
        return maxId + 1;
    }
}