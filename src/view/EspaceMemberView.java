package view;

import controller.ActiviteController;
import controller.InscriptionController;
import dao.ActiviteDAO;
import model.Activite;
import model.Inscription;
import model.Membre;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class EspaceMemberView {

    private ActiviteController activiteController         = new ActiviteController();
    private InscriptionController inscriptionController   = new InscriptionController();
    private ActiviteDAO activiteDAO                       = new ActiviteDAO();

    private TableView<Activite> tableauActivites;
    private TableView<Inscription> tableauMesInscriptions;
    private Membre membre;

    // Style bouton principal (bleu foncé)
    private String styleBoutonPrincipal =
        "-fx-background-color: #2c3e50; -fx-text-fill: white;" +
        "-fx-font-size: 13px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";

    // Style bouton danger (rouge bordure)
    private String styleBoutonDanger =
        "-fx-background-color: transparent; -fx-text-fill: #e74c3c;" +
        "-fx-border-color: #e74c3c; -fx-border-radius: 6; -fx-background-radius: 6;" +
        "-fx-font-size: 13px; -fx-padding: 8 16; -fx-cursor: hand;";

    public void afficher(Stage stage, Membre membre) {
        this.membre = membre;
        stage.setTitle("Espace membre - " + membre.getPrenom());

        Tab ongletActivites    = new Tab("Activités disponibles", creerPanneauActivites());
        Tab ongletInscriptions = new Tab("Mes inscriptions", creerPanneauMesInscriptions());

        TabPane onglets = new TabPane(ongletActivites, ongletInscriptions);
        onglets.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Button btnDeconnexion = new Button("🚪  Se déconnecter");
        btnDeconnexion.setStyle(styleBoutonDanger);
        btnDeconnexion.setOnAction(e -> new LoginView().afficher(stage));

        HBox barreBase = new HBox(btnDeconnexion);
        barreBase.setAlignment(Pos.CENTER_RIGHT);
        barreBase.setPadding(new Insets(5, 15, 10, 15));

        VBox layout = new VBox(10, onglets, barreBase);
        VBox.setVgrow(onglets, Priority.ALWAYS);

        stage.setScene(new Scene(layout, 700, 480));
        stage.show();
    }

    private VBox creerPanneauActivites() {
        tableauActivites = new TableView<>();

        TableColumn<Activite, String> colNom     = new TableColumn<>("Activité");
        TableColumn<Activite, String> colHoraire = new TableColumn<>("Horaire");
        TableColumn<Activite, String> colPlaces  = new TableColumn<>("Places restantes");

        colNom.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().getNom()));
        colHoraire.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraire()));
        colPlaces.setCellValueFactory(c  -> {
            int places = activiteController.getPlacesRestantes(c.getValue().getId());
            return new SimpleStringProperty(places + " / " + c.getValue().getCapaciteMax());
        });

        tableauActivites.getColumns().addAll(colNom, colHoraire, colPlaces);
        rafraichirActivites();

        Button btnInscrire = new Button("✅  S'inscrire");
        btnInscrire.setStyle(styleBoutonPrincipal);
        btnInscrire.setOnAction(e -> sInscrire());

        HBox barre = new HBox(btnInscrire);
        barre.setAlignment(Pos.CENTER_RIGHT);
        barre.setPadding(new Insets(8, 0, 0, 0));

        VBox panneau = new VBox(10, tableauActivites, barre);
        panneau.setPadding(new Insets(10));
        VBox.setVgrow(tableauActivites, Priority.ALWAYS);
        return panneau;
    }

    private VBox creerPanneauMesInscriptions() {
        tableauMesInscriptions = new TableView<>();

        TableColumn<Inscription, String> colActivite = new TableColumn<>("Activité");
        TableColumn<Inscription, String> colStatut   = new TableColumn<>("Statut");

        colActivite.setCellValueFactory(c -> {
            String nom = "?";
            for (Activite a : activiteDAO.getTous()) {
                if (a.getId() == c.getValue().getActiviteId()) {
                    nom = a.getNom();
                    break;
                }
            }
            return new SimpleStringProperty(nom);
        });
        colStatut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatut().toString()));

        tableauMesInscriptions.getColumns().addAll(colActivite, colStatut);
        rafraichirMesInscriptions();

        Button btnAnnuler = new Button("❌  Annuler mon inscription");
        btnAnnuler.setStyle(styleBoutonDanger);
        btnAnnuler.setOnAction(e -> annulerInscription());

        HBox barre = new HBox(btnAnnuler);
        barre.setAlignment(Pos.CENTER_RIGHT);
        barre.setPadding(new Insets(8, 0, 0, 0));

        VBox panneau = new VBox(10, tableauMesInscriptions, barre);
        panneau.setPadding(new Insets(10));
        VBox.setVgrow(tableauMesInscriptions, Priority.ALWAYS);
        return panneau;
    }

    private void sInscrire() {
        Activite selectionne = tableauActivites.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une activité.", ButtonType.OK).showAndWait();
            return;
        }
        try {
            inscriptionController.inscrire(membre.getId(), selectionne.getId());
            rafraichirActivites();
            rafraichirMesInscriptions();
            new Alert(Alert.AlertType.INFORMATION, "Inscription envoyée ! En attente de validation.", ButtonType.OK).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void annulerInscription() {
        Inscription selectionne = tableauMesInscriptions.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une inscription.", ButtonType.OK).showAndWait();
            return;
        }
        inscriptionController.annuler(selectionne.getId());
        rafraichirMesInscriptions();
    }

    private void rafraichirActivites() {
        tableauActivites.setItems(FXCollections.observableArrayList(activiteController.getToutesActivites()));
    }

    private void rafraichirMesInscriptions() {
        List<Inscription> mesInscriptions = inscriptionController.getInscriptionsMembre(membre.getId());
        tableauMesInscriptions.setItems(FXCollections.observableArrayList(mesInscriptions));
    }
}