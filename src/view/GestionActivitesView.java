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
import java.util.List;

public class GestionActivitesView {

    private ActiviteController activiteController = new ActiviteController();
    private TableView<Activite> tableau;

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

        Label titre = new Label("🏃  Gestion des activités");
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

        stage.setScene(new Scene(layout, 720, 460));
        stage.show();
    }

    private TableView<Activite> creerTableau() {
        TableView<Activite> tv = new TableView<>();

        tv.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        tv.setFixedCellSize(42);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ── Row factory: light blue highlight on selection ─────────────────
        tv.setRowFactory(tableView -> new TableRow<Activite>() {
            @Override
            protected void updateItem(Activite activite, boolean empty) {
                super.updateItem(activite, empty);
                styleProperty().unbind();

                if (empty || activite == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    tableView.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldVal, newVal) -> appliquerStyle(this)
                    );
                    appliquerStyle(this);
                }
            }

            private void appliquerStyle(TableRow<Activite> row) {
                if (row.isSelected()) {
                    row.setStyle(
                        "-fx-background-color: #d6eaf8;" +
                        "-fx-border-color: #2980b9;" +
                        "-fx-border-width: 0 0 0 4;"
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

        // ── Columns (no text — titles carried by white Label) ──────────────
        TableColumn<Activite, String> colNom      = new TableColumn<>();
        TableColumn<Activite, String> colHoraire  = new TableColumn<>();
        TableColumn<Activite, Number> colCapacite = new TableColumn<>();
        TableColumn<Activite, String> colDesc     = new TableColumn<>();

        colNom.setPrefWidth(140);
        colHoraire.setPrefWidth(160);
        colCapacite.setPrefWidth(110);
        colDesc.setPrefWidth(280);

        // ── White header labels ─────────────────────────────────────────────
        String[] titresColonnes = {"Nom", "Horaire", "Capacité max", "Description"};
        List<TableColumn<Activite, ?>> colonnes = List.of(colNom, colHoraire, colCapacite, colDesc);

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

        // ── Cell value factories ────────────────────────────────────────────
        colNom.setCellValueFactory(c      -> new SimpleStringProperty(c.getValue().getNom()));
        colHoraire.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getHoraire()));
        colCapacite.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCapaciteMax()));
        colDesc.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().getDescription()));

        // ── Cell factories ──────────────────────────────────────────────────
        colNom.setCellFactory(col     -> celluleStylee());
        colHoraire.setCellFactory(col -> celluleStylee());
        colDesc.setCellFactory(col    -> celluleStylee());

        // Capacité: colored badge normally, plain text when row is selected
        colCapacite.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean selected = getTableRow() != null && getTableRow().isSelected();
                    if (selected) {
                        // Row highlighted — plain text, transparent bg
                        setText(String.valueOf(item.intValue()));
                        setGraphic(null);
                        setStyle(
                            "-fx-padding: 0 10;" +
                            "-fx-font-size: 13px;" +
                            "-fx-text-fill: #1a5276;" +
                            "-fx-background-color: transparent;"
                        );
                    } else {
                        // Normal row — colored badge by capacity level
                        Label badge = new Label(String.valueOf(item.intValue()));
                        badge.setPadding(new Insets(3, 10, 3, 10));
                        String couleur = item.intValue() >= 20 ? "#d5f5e3;-fx-text-fill:#1e8449"
                                       : item.intValue() >= 10 ? "#fef9e7;-fx-text-fill:#b7950b"
                                       :                         "#fadbd8;-fx-text-fill:#c0392b";
                        badge.setStyle(
                            "-fx-background-color: " + couleur + ";" +
                            "-fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;"
                        );
                        setGraphic(badge);
                        setText(null);
                        setStyle(
                            "-fx-alignment: center-left; -fx-padding: 0 10;" +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;"
                        );
                    }
                }
            }
        });

        tv.getColumns().addAll(colNom, colHoraire, colCapacite, colDesc);
        return tv;
    }

    // ── Cell: transparent bg so row highlight shows through ────────────────
    private TableCell<Activite, String> celluleStylee() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    boolean selected = getTableRow() != null && getTableRow().isSelected();
                    setStyle(
                        "-fx-padding: 0 10;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + (selected ? "#1a5276" : "#2c3e50") + ";" +
                        "-fx-border-color: transparent transparent #ecf0f1 transparent;" +
                        "-fx-border-width: 0 0 1 0;"
                    );
                }
            }
        };
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
                activiteController.ajouterActivite(
                    champNom.getText(), champDesc.getText(), capacite, champHoraire.getText()
                );
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
        layout.setStyle("-fx-background-color: white;");

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
        layout.setStyle("-fx-background-color: white;");

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