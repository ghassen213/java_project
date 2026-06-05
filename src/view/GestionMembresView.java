package view;

import controller.MembreController;
import model.Membre;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class GestionMembresView {

    private MembreController membreController = new MembreController();
    private TableView<Membre> tableau;

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
        stage.setTitle("Gestion des membres");

        tableau = creerTableau();
        rafraichirTableau();

        Button btnAjouter   = new Button("➕  Ajouter");
        Button btnModifier  = new Button("✏️  Modifier");
        Button btnSupprimer = new Button("🗑  Supprimer");
        Button btnRetour    = new Button("←  Retour");

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

        stage.setScene(new Scene(layout, 700, 450));
        stage.show();
    }

    private TableView<Membre> creerTableau() {
        TableView<Membre> tv = new TableView<>();

        TableColumn<Membre, String> colNom    = new TableColumn<>("Nom");
        TableColumn<Membre, String> colPrenom = new TableColumn<>("Prénom");
        TableColumn<Membre, String> colLogin  = new TableColumn<>("Login");
        TableColumn<Membre, String> colEmail  = new TableColumn<>("Email");

        colNom.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getNom()));
        colPrenom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrenom()));
        colLogin.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getLogin()));
        colEmail.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getEmail()));

        tv.getColumns().addAll(colNom, colPrenom, colLogin, colEmail);
        return tv;
    }

    private void rafraichirTableau() {
        List<Membre> membres = membreController.getTousMembres();
        tableau.setItems(FXCollections.observableArrayList(membres));
    }

    private void ouvrirFormulaireAjout() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Ajouter un membre");

        TextField champLogin  = new TextField(); champLogin.setPromptText("Login");
        TextField champMdp    = new TextField(); champMdp.setPromptText("Mot de passe");
        TextField champNom    = new TextField(); champNom.setPromptText("Nom");
        TextField champPrenom = new TextField(); champPrenom.setPromptText("Prénom");
        TextField champEmail  = new TextField(); champEmail.setPromptText("Email");
        TextField champTel    = new TextField(); champTel.setPromptText("Téléphone");
        TextField champPoids  = new TextField(); champPoids.setPromptText("Poids (kg)");
        DatePicker datePicker = new DatePicker();

        Label messageErreur = new Label();
        messageErreur.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        Button btnValider = new Button("Valider");
        btnValider.setMaxWidth(Double.MAX_VALUE);
        btnValider.setStyle(stylePrincipal);

        btnValider.setOnAction(e -> {
            try {
                membreController.ajouterMembre(
                        champLogin.getText(), champMdp.getText(),
                        champNom.getText(), champPrenom.getText(),
                        datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now(),
                        "", champTel.getText(), champEmail.getText(),
                        Double.parseDouble(champPoids.getText())
                );
                rafraichirTableau();
                fenetre.close();
            } catch (Exception ex) {
                messageErreur.setText("⚠ " + ex.getMessage());
            }
        });

        VBox layout = new VBox(8, champLogin, champMdp, champNom, champPrenom,
                champEmail, champTel, champPoids, datePicker, btnValider, messageErreur);
        layout.setPadding(new Insets(20));

        fenetre.setScene(new Scene(layout, 300, 420));
        fenetre.show();
    }

    private void ouvrirFormulaireModification() {
        Membre selectionne = tableau.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherAvertissement("Veuillez sélectionner un membre.");
            return;
        }

        Stage fenetre = new Stage();
        fenetre.setTitle("Modifier un membre");

        TextField champNom    = new TextField(selectionne.getNom());
        TextField champPrenom = new TextField(selectionne.getPrenom());
        TextField champEmail  = new TextField(selectionne.getEmail());
        TextField champTel    = new TextField(selectionne.getTelephone());

        Label messageErreur = new Label();
        messageErreur.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        Button btnValider = new Button("Valider");
        btnValider.setMaxWidth(Double.MAX_VALUE);
        btnValider.setStyle(stylePrincipal);

        btnValider.setOnAction(e -> {
            try {
                selectionne.setNom(champNom.getText());
                selectionne.setPrenom(champPrenom.getText());
                selectionne.setEmail(champEmail.getText());
                selectionne.setTelephone(champTel.getText());
                membreController.modifierMembre(selectionne);
                rafraichirTableau();
                fenetre.close();
            } catch (Exception ex) {
                messageErreur.setText("⚠ " + ex.getMessage());
            }
        });

        VBox layout = new VBox(8,
                new Label("Nom:"), champNom,
                new Label("Prénom:"), champPrenom,
                new Label("Email:"), champEmail,
                new Label("Téléphone:"), champTel,
                btnValider, messageErreur);
        layout.setPadding(new Insets(20));

        fenetre.setScene(new Scene(layout, 300, 340));
        fenetre.show();
    }

    private void supprimerSelectionne() {
        Membre selectionne = tableau.getSelectionModel().getSelectedItem();
        if (selectionne == null) {
            afficherAvertissement("Veuillez sélectionner un membre.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer " + selectionne.getNom() + " ?", ButtonType.YES, ButtonType.NO);
        confirmation.showAndWait().ifPresent(reponse -> {
            if (reponse == ButtonType.YES) {
                membreController.supprimerMembre(selectionne.getId());
                rafraichirTableau();
            }
        });
    }

    private void afficherAvertissement(String message) {
        new Alert(Alert.AlertType.WARNING, message, ButtonType.OK).showAndWait();
    }
}