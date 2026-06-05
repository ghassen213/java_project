package model;

import java.io.Serializable;

public class Inscription implements Serializable {

    // Les 3 états possibles d'une inscription
    public enum Statut {
        EN_ATTENTE,
        ACCEPTEE,
        REFUSEE
    }

    private int id;
    private int membreId;
    private int activiteId;
    private Statut statut;

    public Inscription(int id, int membreId, int activiteId) {
        this.id = id;
        this.membreId = membreId;
        this.activiteId = activiteId;
        this.statut = Statut.EN_ATTENTE; // par défaut : en attente
    }

    public int getId() { return id; }
    public int getMembreId() { return membreId; }
    public int getActiviteId() { return activiteId; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
}
