package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire pour lire et écrire n'importe quelle liste dans un fichier binaire.
 * On l'utilise pour Membre, Activite et Inscription.
 */
public class FichierUtil {

    // Lit une liste d'objets depuis un fichier binaire
    @SuppressWarnings("unchecked")
    public static <T> List<T> lire(String cheminFichier) {
        File fichier = new File(cheminFichier);
        if (!fichier.exists()) {
            return new ArrayList<>(); // fichier pas encore créé = liste vide
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichier))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Erreur lecture fichier : " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Écrit une liste d'objets dans un fichier binaire
    public static <T> void ecrire(String cheminFichier, List<T> liste) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cheminFichier))) {
            oos.writeObject(liste);
        } catch (Exception e) {
            System.err.println("Erreur écriture fichier : " + e.getMessage());
        }
    }
}
