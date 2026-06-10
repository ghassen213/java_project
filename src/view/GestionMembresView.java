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

        Label titre = new Label("👤  Gestion des membres");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

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
        barreActions.setPadding(new Insets(10, 0, 0, 0));

        VBox layout = new VBox(12, titre, tableau, barreActions);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #f4f6f8;");
        VBox.setVgrow(tableau, Priority.ALWAYS);

        stage.setScene(new Scene(layout, 750, 480));
        stage.show();
    }

    private TableView<Membre> creerTableau() {
        TableView<Membre> tv = new TableView<>();

        tv.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        tv.setFixedCellSize(42);

        // ── Row factory: highlight selected row in light blue ──────────────
        tv.setRowFactory(tableView -> new TableRow<Membre>() {
            @Override
            protected void updateItem(Membre membre, boolean empty) {
                super.updateItem(membre, empty);

                // Clear any previous listener-driven style first
                styleProperty().unbind();

                if (empty || membre == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Re-apply style whenever selection state changes
                    tableView.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldVal, newVal) -> appliquerStyle(this)
                    );
                    appliquerStyle(this);
                }
            }

            private void appliquerStyle(TableRow<Membre> row) {
                if (row.isSelected()) {
                    row.setStyle(
                        "-fx-background-color: #d6eaf8;" +   // light blue fill
                        "-fx-border-color: #2980b9;" +       // blue left accent border
                        "-fx-border-width: 0 0 0 4;"         // only left side
                    );
                } else {
                    int idx = row.getIndex();
                    row.setStyle(
                        "-fx-background-color: " + (idx % 2 == 0 ? "white" : "#f8f9fa") + ";" +
                        "-fx-border-color: transparent transparent #ecf0f1 transparent;" +
                        "-fx-border-width: 0 0 1 0;"
                    );
                }
            }
        });

        TableColumn<Membre, String> colNom    = new TableColumn<>();
        TableColumn<Membre, String> colPrenom = new TableColumn<>();
        TableColumn<Membre, String> colLogin  = new TableColumn<>();
        TableColumn<Membre, String> colEmail  = new TableColumn<>();

        colNom.setPrefWidth(150);
        colPrenom.setPrefWidth(150);
        colLogin.setPrefWidth(130);
        colEmail.setPrefWidth(280);

        colNom.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getNom()));
        colPrenom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrenom()));
        colLogin.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getLogin()));
        colEmail.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getEmail()));

        colNom.setCellFactory(col    -> celluleStylee());
        colPrenom.setCellFactory(col -> celluleStylee());
        colLogin.setCellFactory(col  -> celluleStylee());
        colEmail.setCellFactory(col  -> celluleStylee());

        // --- En-têtes avec Label blanc ---
        String[] titresColonnes = {"Nom", "Prénom", "Login", "Email"};
        List<TableColumn<Membre, String>> colonnes = List.of(colNom, colPrenom, colLogin, colEmail);

        for (int i = 0; i < colonnes.size(); i++) {
            Label labelEntete = new Label(titresColonnes[i]);
            labelEntete.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
            );
            colonnes.get(i).setGraphic(labelEntete);
            colonnes.get(i).setStyle("-fx-background-color: #2c3e50; -fx-padding: 10 0;");
        }

        tv.getColumns().addAll(colNom, colPrenom, colLogin, colEmail);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }

    // ── Cell: text color adapts when row is selected ───────────────────────
    private TableCell<Membre, String> celluleStylee() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    // Keep cell background transparent so the ROW color shows through
                    // Only control text color: darker blue when selected for contrast
                    boolean selected = getTableRow() != null && getTableRow().isSelected();
                    setStyle(
                        "-fx-padding: 0 10;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + (selected ? "#1a5276" : "#2c3e50") + ";"
                    );
                }
            }
        };
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
        layout.setStyle("-fx-background-color: white;");

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
        layout.setStyle("-fx-background-color: white;");

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