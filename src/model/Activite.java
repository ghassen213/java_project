package model;

import java.io.Serializable;

public class Activite implements Serializable {

    private int id;
    private String nom;
    private String description;
    private int capaciteMax;
    private String horaire; // ex: "Lundi 10h-12h"

    public Activite(int id, String nom, String description, int capaciteMax, String horaire) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.capaciteMax = capaciteMax;
        this.horaire = horaire;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCapaciteMax() { return capaciteMax; }
    public void setCapaciteMax(int capaciteMax) { this.capaciteMax = capaciteMax; }
    public String getHoraire() { return horaire; }
    public void setHoraire(String horaire) { this.horaire = horaire; }

    @Override
    public String toString() {
        return nom + " - " + horaire;
    }
}
