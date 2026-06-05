package view;

import controller.ActiviteController;
import model.Activite;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GestionActivitesView {

    private ActiviteController activiteController = new ActiviteController();
    private TableView<Activite> tableau;

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
        stage.setTitle("Gestion des activités");

        tableau = creerTableau();
        rafraichirTableau();

        Button btnAjouter   = new Button("➕  Ajouter");
        Button btnModifier  = new Button("✏️  Modifier");
        Button btnSupprimer = new Button("🗑  Supprimer");
        Button btnRetour    = new Button("← Retour");

        btnAjouter.setStyle(stylePrincipal);
        btnModifier.setStyle(styleSecondaire);
        btnSupprimer.setStyle(styleDanger);
        btnRetour.setStyle(styleSecondaire);

        btnAjouter.setOnAction(e   -> ouvrirFormulaireAjout());
        btnModifier.setOnAction(e  -> ouvrirFormulaireModification());
        btnSupprimer.setOnAction(e -> supprimerSelectionne());
        btnRetour.setOnAction(e    -> new DashboardAdminView().afficher(stage));

        HBox barreActions = new HBox(10, btnAjouter, btnModifier, btnSupprimer, btnRetour);
        barreActions.setAlignment(Pos.CENTER_RIGHT);
        barreActions.setPadding(new Insets(10, 15, 10, 15));

        VBox layout = new VBox(10, tableau, barreActions);
        layout.setPadding(new Insets(15));
        VBox.setVgrow(tableau, Priority.ALWAYS);

        stage.setScene(new Scene(layout, 680, 420));
        stage.show();
    }

    private TableView<Activite> creerTableau() {
        TableView<Activite> tv = new TableView<>();

        TableColumn<Activite, String> colNom      = new TableColumn<>("Nom");
        TableColumn<Activite, String> colHoraire  = new TableColumn<>("Horaire");
        TableColumn<Activite, Number> colCapacite = new TableColumn<>("Capacité max");
        TableColumn<Activite, String> colDesc     = new TableColumn<>("Description");

        colNom.setCellValueFactory(c      -> new SimpleStringProperty(c.getValue().getNom()));
        colHoraire.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getHoraire()));
        colCapacite.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCapaciteMax()));
        colDesc.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().getDescription()));

        tv.getColumns().addAll(colNom, colHoraire, colCapacite, colDesc);
        return tv;
    }

    private void rafraichirTableau() {
        tableau.setItems(FXCollections.observableArrayList(activiteController.getToutesActivites()));
    }

    private void ouvrirFormulaireAjout() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Ajouter une activité");

        TextField champNom      = new TextField(); champNom.setPromptText("Nom (ex: Yoga)");
        TextField champDesc     = new TextField(); champDesc.setPromptText("Description");
        TextField champCapacite = new TextField(); champCapacite.setPromptText("Capacité max");
        TextField champHoraire  = new TextField(); champHoraire.setPromptText("Horaire (ex: Lundi 10h-12h)");

        Label messageErreur = new Label();
        messageErreur.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        Button btnValider = new Button("Valider");
        btnValider.setMaxWidth(Double.MAX_VALUE);
        btnValider.setStyle(stylePrincipal);

        btnValider.setOnAction(e -> {
            try {
                int capacite = Integer.parseInt(champCapacite.getText());
                activiteController.ajouterActivite(champNom.getText(), champDesc.getText(), capacite, champHoraire.getText());
                rafraichirTableau();
                fenetre.close();
            } catch (NumberFormatException ex) {
                messageErreur.setText("⚠ La capacité doit être un nombre entier.");
            } catch (Exception ex) {
                messageErreur.setText("⚠ " + ex.getMessage());
            }
        });

        VBox layout = new VBox(8, champNom, champDesc, champCapacite, champHoraire, btnValider, messageErreur);
        layout.setPadding(new Insets(20));

        fenetre.setScene(new Scene(layout, 300, 280));
        fenetre.show();
    }

    private void ouvrirFormulaireModification() {
        Activite selectionne = tableau.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une activité.", ButtonType.OK).showAndWait();
            return;
        }

        Stage fenetre = new Stage();
        fenetre.setTitle("Modifier une activité");

        TextField champNom      = new TextField(selectionne.getNom());
        TextField champDesc     = new TextField(selectionne.getDescription());
        TextField champCapacite = new TextField(String.valueOf(selectionne.getCapaciteMax()));
        TextField champHoraire  = new TextField(selectionne.getHoraire());

        Label messageErreur = new Label();
        messageErreur.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        Button btnValider = new Button("Valider");
        btnValider.setMaxWidth(Double.MAX_VALUE);
        btnValider.setStyle(stylePrincipal);

        btnValider.setOnAction(e -> {
            try {
                selectionne.setNom(champNom.getText());
                selectionne.setDescription(champDesc.getText());
                selectionne.setCapaciteMax(Integer.parseInt(champCapacite.getText()));
                selectionne.setHoraire(champHoraire.getText());
                activiteController.modifierActivite(selectionne);
                rafraichirTableau();
                fenetre.close();
            } catch (Exception ex) {
                messageErreur.setText("⚠ " + ex.getMessage());
            }
        });

        VBox layout = new VBox(8,
                new Label("Nom:"), champNom,
                new Label("Description:"), champDesc,
                new Label("Capacité:"), champCapacite,
                new Label("Horaire:"), champHoraire,
                btnValider, messageErreur);
        layout.setPadding(new Insets(20));

        fenetre.setScene(new Scene(layout, 300, 350));
        fenetre.show();
    }

    private void supprimerSelectionne() {
        Activite selectionne = tableau.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une activité.", ButtonType.OK).showAndWait();
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer l'activité \"" + selectionne.getNom() + "\" ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(rep -> {
            if (rep == ButtonType.YES) {
                activiteController.supprimerActivite(selectionne.getId());
                rafraichirTableau();
            }
        });
    }
}