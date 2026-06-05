package view;

import controller.InscriptionController;
import dao.ActiviteDAO;
import dao.MembreDAO;
import model.Inscription;
import model.Membre;
import model.Activite;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class GestionInscriptionsView {

    private InscriptionController inscriptionController = new InscriptionController();
    private MembreDAO membreDAO                         = new MembreDAO();
    private ActiviteDAO activiteDAO                     = new ActiviteDAO();
    private TableView<Inscription> tableau;

    // Styles partagés
    private String stylePrincipal =
        "-fx-background-color: #2c3e50; -fx-text-fill: white;" +
        "-fx-font-size: 13px; -fx-padding: 8 14; -fx-background-radius: 6; -fx-cursor: hand;";

    private String styleDanger =
        "-fx-background-color: transparent; -fx-text-fill: #e74c3c;" +
        "-fx-border-color: #e74c3c; -fx-border-radius: 6; -fx-background-radius: 6;" +
        "-fx-font-size: 13px; -fx-padding: 8 14; -fx-cursor: hand;";

    private String styleSecondaire =
        "-fx-background-color: transparent; -fx-text-fill: #2c3e50;" +
        "-fx-border-color: #2c3e50; -fx-border-radius: 6; -fx-background-radius: 6;" +
        "-fx-font-size: 13px; -fx-padding: 8 14; -fx-cursor: hand;";

    public void afficher(Stage stage) {
        stage.setTitle("Gestion des inscriptions");

        tableau = creerTableau();
        rafraichirTableau();

        Button btnValider      = new Button("✅  Valider");
        Button btnRefuser      = new Button("❌  Refuser");
        Button btnSupprimer    = new Button("🗑  Supprimer");
        Button btnParticipants = new Button("👥  Participants");
        Button btnRetour       = new Button("←  Retour");

        btnValider.setStyle(stylePrincipal);
        btnRefuser.setStyle(styleDanger);
        btnSupprimer.setStyle(styleDanger);
        btnParticipants.setStyle(styleSecondaire);
        btnRetour.setStyle(styleSecondaire);

        btnValider.setOnAction(e      -> changerStatutSelectionne(true));
        btnRefuser.setOnAction(e      -> changerStatutSelectionne(false));
        btnSupprimer.setOnAction(e    -> supprimerSelectionne());
        btnParticipants.setOnAction(e -> afficherParticipantsParActivite());
        btnRetour.setOnAction(e       -> new DashboardAdminView().afficher(stage));

        HBox barreActions = new HBox(10, btnValider, btnRefuser, btnSupprimer, btnParticipants, btnRetour);
        barreActions.setAlignment(Pos.CENTER_RIGHT);
        barreActions.setPadding(new Insets(10, 15, 10, 15));

        VBox layout = new VBox(10, tableau, barreActions);
        layout.setPadding(new Insets(15));
        VBox.setVgrow(tableau, Priority.ALWAYS);

        stage.setScene(new Scene(layout, 800, 420));
        stage.show();
    }

    private TableView<Inscription> creerTableau() {
        TableView<Inscription> tv = new TableView<>();

        TableColumn<Inscription, String> colMembre   = new TableColumn<>("Membre");
        TableColumn<Inscription, String> colActivite = new TableColumn<>("Activité");
        TableColumn<Inscription, String> colStatut   = new TableColumn<>("Statut");

        // Chercher le nom du membre à partir de son id
        colMembre.setCellValueFactory(c -> {
            String nom = "Inconnu";
            for (Membre m : membreDAO.getTous()) {
                if (m.getId() == c.getValue().getMembreId()) {
                    nom = m.getPrenom() + " " + m.getNom();
                    break;
                }
            }
            return new SimpleStringProperty(nom);
        });

        // Chercher le nom de l'activité à partir de son id
        colActivite.setCellValueFactory(c -> {
            String nom = "Inconnu";
            for (Activite a : activiteDAO.getTous()) {
                if (a.getId() == c.getValue().getActiviteId()) {
                    nom = a.getNom();
                    break;
                }
            }
            return new SimpleStringProperty(nom);
        });

        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut().toString()));

        tv.getColumns().addAll(colMembre, colActivite, colStatut);
        return tv;
    }

    private void rafraichirTableau() {
        tableau.setItems(FXCollections.observableArrayList(inscriptionController.getToutesInscriptions()));
    }

    private void changerStatutSelectionne(boolean valider) {
        Inscription selectionne = tableau.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une inscription.", ButtonType.OK).showAndWait();
            return;
        }
        if (valider) {
            inscriptionController.valider(selectionne.getId());
        } else {
            inscriptionController.refuser(selectionne.getId());
        }
        rafraichirTableau();
    }

    private void afficherParticipantsParActivite() {
        StringBuilder contenu = new StringBuilder();

        for (Activite activite : activiteDAO.getTous()) {
            int nbParticipants = 0;
            for (Inscription i : inscriptionController.getInscriptionsActivite(activite.getId())) {
                if (i.getStatut() == Inscription.Statut.ACCEPTEE) nbParticipants++;
            }
            contenu.append(activite.getNom())
                   .append(" : ")
                   .append(nbParticipants)
                   .append(" / ")
                   .append(activite.getCapaciteMax())
                   .append(" participants\n");
        }

        if (contenu.isEmpty()) contenu.append("Aucune activité enregistrée.");

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Participants par activité");
        info.setHeaderText("Nombre de participants acceptés");
        info.setContentText(contenu.toString());
        info.showAndWait();
    }

    private void supprimerSelectionne() {
        Inscription selectionne = tableau.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une inscription.", ButtonType.OK).showAndWait();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer cette inscription ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(rep -> {
            if (rep == ButtonType.YES) {
                inscriptionController.annuler(selectionne.getId());
                rafraichirTableau();
            }
        });
    }
}