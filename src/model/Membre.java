package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Membre implements Serializable {

    private int id;
    private String login;
    private String motDePasse;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String adresse;
    private String telephone;
    private String email;
    private double poids;
    private boolean premierAcces; // pour forcer le changement de mot de passe

    public Membre(int id, String login, String motDePasse, String nom, String prenom,
                  LocalDate dateNaissance, String adresse, String telephone, String email, double poids) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.poids = poids;
        this.premierAcces = true;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getLogin() { return login; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public double getPoids() { return poids; }
    public void setPoids(double poids) { this.poids = poids; }
    public boolean isPremierAcces() { return premierAcces; }
    public void setPremierAcces(boolean premierAcces) { this.premierAcces = premierAcces; }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + login + ")";
    }
}
