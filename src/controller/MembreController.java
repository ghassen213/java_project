package controller;

import dao.InscriptionDAO;
import dao.MembreDAO;
import model.Membre;
import java.time.LocalDate;
import java.util.List;

public class MembreController {

    private MembreDAO membreDAO = new MembreDAO();
    private InscriptionDAO inscriptionDAO = new InscriptionDAO(); 

    // Retourne tous les membres
    public List<Membre> getTousMembres() {
        return membreDAO.getTous();
    }

    // Ajoute un nouveau membre après vérification
    public void ajouterMembre(String login, String mdp, String nom, String prenom,LocalDate dateNaissance, String adresse,String telephone, String email, double poids) {

        // Vérifier que le login n'existe pas déjà
        if (membreDAO.trouverParLogin(login) != null)
            throw new IllegalArgumentException("Ce login est déjà utilisé.");
         // ── Champs obligatoires vides ──────────────────────────────────────────
if (login.isBlank() || mdp.isBlank() || nom.isBlank() ||
    prenom.isBlank() || telephone.isBlank() || email.isBlank())
    throw new IllegalArgumentException("Tous les champs sont obligatoires.");

// ── Nom : lettres et espaces uniquement, pas de chiffres ──────────────
if (!nom.matches("[a-zA-ZÀ-ÿ\\s]+"))
    throw new IllegalArgumentException("Le nom ne doit pas contenir de chiffres.");

// ── Prénom : lettres et espaces uniquement, pas de chiffres ───────────
if (!prenom.matches("[a-zA-ZÀ-ÿ\\s]+"))
    throw new IllegalArgumentException("Le prénom ne doit pas contenir de chiffres.");

// ── Téléphone : convertir String → int puis valider ───────────────────
int tel;
try {
    tel = Integer.parseInt(telephone.trim());
} catch (NumberFormatException e) {
    throw new IllegalArgumentException("Le téléphone doit contenir uniquement des chiffres.");
}
String telStr = String.valueOf(tel);
if (telStr.length() != 8)
    throw new IllegalArgumentException("Le numéro doit contenir exactement 8 chiffres.");
if (!telStr.matches("[25793].*"))
    throw new IllegalArgumentException("Le numéro doit commencer par 2, 3, 5, 7 ou 9.");

    // Vérifier que le poids est valide
    if (poids <= 0)
        throw new IllegalArgumentException("Le poids doit être supérieur à 0.");
        int id = membreDAO.genererNouvelId();
        Membre nouveau = new Membre(id, login, mdp, nom, prenom, dateNaissance, adresse, telephone, email, poids);
        membreDAO.ajouter(nouveau);
    }

    // Modifie un membre existant
   public void modifierMembre(Membre membre) {

    // ── Champs obligatoires vides ──────────────────────────────────────
    if (membre.getNom().isBlank() || membre.getPrenom().isBlank() ||
        membre.getEmail().isBlank())
        throw new IllegalArgumentException("Tous les champs sont obligatoires.");

    // ── Nom : lettres et espaces uniquement, pas de chiffres ──────────
    if (!membre.getNom().matches("[a-zA-ZÀ-ÿ\\s]+"))
        throw new IllegalArgumentException("Le nom ne doit pas contenir de chiffres.");

    // ── Prénom : lettres et espaces uniquement, pas de chiffres ───────
    if (!membre.getPrenom().matches("[a-zA-ZÀ-ÿ\\s]+"))
        throw new IllegalArgumentException("Le prénom ne doit pas contenir de chiffres.");

    // ── Téléphone : convertir String → int puis valider ───────────────
    int tel;
    try {
        tel = Integer.parseInt(String.valueOf(membre.getTelephone()).trim());
    } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Le téléphone doit contenir uniquement des chiffres.");
    }
    String telStr = String.valueOf(tel);
    if (telStr.length() != 8)
        throw new IllegalArgumentException("Le numéro doit contenir exactement 8 chiffres.");
    if (!telStr.matches("[25793].*"))
        throw new IllegalArgumentException("Le numéro doit commencer par 2, 3, 5, 7 ou 9.");

    membreDAO.modifier(membre);
}

    // Supprime un membre par son id
    public void supprimerMembre(int id) {
        inscriptionDAO.getParMembre(id).forEach(i -> inscriptionDAO.supprimer(i.getId()));
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