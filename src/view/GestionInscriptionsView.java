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

        Label titre = new Label("📋  Gestion des inscriptions");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

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
        barreActions.setPadding(new Insets(10, 0, 0, 0));

        VBox layout = new VBox(12, titre, tableau, barreActions);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #f4f6f8;");
        VBox.setVgrow(tableau, Priority.ALWAYS);

        stage.setScene(new Scene(layout, 820, 460));
        stage.show();
    }

    private TableView<Inscription> creerTableau() {
        TableView<Inscription> tv = new TableView<>();

        tv.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        tv.setFixedCellSize(42);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ── Row factory: highlight selected row in light blue ──────────────
        tv.setRowFactory(tableView -> new TableRow<Inscription>() {
            @Override
            protected void updateItem(Inscription inscription, boolean empty) {
                super.updateItem(inscription, empty);
                styleProperty().unbind();

                if (empty || inscription == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    tableView.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldVal, newVal) -> appliquerStyle(this)
                    );
                    appliquerStyle(this);
                }
            }

            private void appliquerStyle(TableRow<Inscription> row) {
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
        TableColumn<Inscription, String> colMembre   = new TableColumn<>();
        TableColumn<Inscription, String> colActivite = new TableColumn<>();
        TableColumn<Inscription, String> colStatut   = new TableColumn<>();

        colMembre.setPrefWidth(220);
        colActivite.setPrefWidth(220);
        colStatut.setPrefWidth(160);

        // ── White header labels ─────────────────────────────────────────────
        String[] titresColonnes = {"Membre", "Activité", "Statut"};
        List<TableColumn<Inscription, String>> colonnes = List.of(colMembre, colActivite, colStatut);

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

        colStatut.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getStatut().toString())
        );

        // ── Cell factories ──────────────────────────────────────────────────
        colMembre.setCellFactory(col   -> celluleStylee());
        colActivite.setCellFactory(col -> celluleStylee());

        // Statut cell: badge when normal, plain text when selected
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean selected = getTableRow() != null && getTableRow().isSelected();
                    if (selected) {
                        // Row is highlighted — show plain text, transparent bg
                        setText(item);
                        setGraphic(null);
                        setStyle(
                            "-fx-padding: 0 10;" +
                            "-fx-font-size: 13px;" +
                            "-fx-text-fill: #1a5276;" +
                            "-fx-background-color: transparent;"
                        );
                    } else {
                        // Normal row — colored badge
                        Label badge = new Label(item);
                        badge.setPadding(new Insets(3, 10, 3, 10));
                        badge.setStyle(getBadgeStyle(item));
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

            private String getBadgeStyle(String statut) {
                return switch (statut) {
                    case "ACCEPTEE" ->
                        "-fx-background-color: #d5f5e3; -fx-text-fill: #1e8449;" +
                        "-fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;";
                    case "REFUSEE" ->
                        "-fx-background-color: #fadbd8; -fx-text-fill: #c0392b;" +
                        "-fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;";
                    default ->
                        "-fx-background-color: #fef9e7; -fx-text-fill: #b7950b;" +
                        "-fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;";
                };
            }
        });

        tv.getColumns().addAll(colMembre, colActivite, colStatut);
        return tv;
    }

    // ── Cell: transparent bg so row highlight shows through ────────────────
    private TableCell<Inscription, String> celluleStylee() {
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