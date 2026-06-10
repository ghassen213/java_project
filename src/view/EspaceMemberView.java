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
import util.cardpdf;

public class EspaceMemberView {

    private ActiviteController activiteController       = new ActiviteController();
    private InscriptionController inscriptionController = new InscriptionController();
    private ActiviteDAO activiteDAO                     = new ActiviteDAO();

    private TableView<Activite> tableauActivites;
    private TableView<Inscription> tableauMesInscriptions;
    private Membre membre;

    private String styleBoutonPrincipal =
        "-fx-background-color: #2c3e50; -fx-text-fill: white;" +
        "-fx-font-size: 13px; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;";

    private String styleBoutonDanger =
        "-fx-background-color: transparent; -fx-text-fill: #e74c3c;" +
        "-fx-border-color: #e74c3c; -fx-border-radius: 6; -fx-background-radius: 6;" +
        "-fx-font-size: 13px; -fx-padding: 8 16; -fx-cursor: hand;";

    public void afficher(Stage stage, Membre membre) {
        this.membre = membre;
        stage.setTitle("Espace membre - " + membre.getPrenom());

        Tab ongletActivites    = new Tab("🏃  Activités disponibles", creerPanneauActivites());
        Tab ongletInscriptions = new Tab("📋  Mes inscriptions", creerPanneauMesInscriptions());

        TabPane onglets = new TabPane(ongletActivites, ongletInscriptions);
        onglets.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        onglets.setStyle("-fx-background-color: #f4f6f8;");

        Button btnDeconnexion = new Button("🚪  Se déconnecter");
        btnDeconnexion.setStyle(styleBoutonDanger);
        btnDeconnexion.setOnAction(e -> new LoginView().afficher(stage));

        HBox barreBase = new HBox(btnDeconnexion);
        barreBase.setAlignment(Pos.CENTER_RIGHT);
        barreBase.setPadding(new Insets(8, 20, 12, 20));
        barreBase.setStyle("-fx-background-color: #f4f6f8;");

        VBox layout = new VBox(0, onglets, barreBase);
        layout.setStyle("-fx-background-color: #f4f6f8;");
        VBox.setVgrow(onglets, Priority.ALWAYS);

        stage.setScene(new Scene(layout, 750, 500));
        stage.show();
    }

    // ===================== PANNEAU ACTIVITES =====================

    private VBox creerPanneauActivites() {
        tableauActivites = new TableView<>();
        appliquerStyleTableau(tableauActivites);

        // ── Row highlight for activities table ─────────────────────────────
        tableauActivites.setRowFactory(tv -> new TableRow<Activite>() {
            @Override
            protected void updateItem(Activite activite, boolean empty) {
                super.updateItem(activite, empty);
                styleProperty().unbind();
                if (empty || activite == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    tv.getSelectionModel().selectedItemProperty().addListener(
                        (obs, o, n) -> appliquerStyle(this)
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

        TableColumn<Activite, String> colNom     = new TableColumn<>("Activité");
        TableColumn<Activite, String> colHoraire = new TableColumn<>("Horaire");
        TableColumn<Activite, String> colPlaces  = new TableColumn<>("Places restantes");

        colNom.setPrefWidth(220);
        colHoraire.setPrefWidth(200);
        colPlaces.setPrefWidth(150);

        appliquerStyleEntete(colNom, colHoraire, colPlaces);

        colNom.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().getNom()));
        colHoraire.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoraire()));
        colPlaces.setCellValueFactory(c  -> {
            int places = activiteController.getPlacesRestantes(c.getValue().getId());
            return new SimpleStringProperty(places + " / " + c.getValue().getCapaciteMax());
        });

        colNom.setCellFactory(col     -> celluleStyleeActivite());
        colHoraire.setCellFactory(col -> celluleStyleeActivite());

        // Places badge: transparent bg when selected so row color shows through
        colPlaces.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean selected = getTableRow() != null && getTableRow().isSelected();
                    if (selected) {
                        setText(item); setGraphic(null);
                        setStyle(
                            "-fx-padding: 0 10; -fx-font-size: 13px;" +
                            "-fx-text-fill: #1a5276;" +
                            "-fx-background-color: transparent;"
                        );
                    } else {
                        int places = Integer.parseInt(item.split(" / ")[0].trim());
                        String couleur = places == 0  ? "#fadbd8;-fx-text-fill:#c0392b"
                                       : places <= 3  ? "#fef9e7;-fx-text-fill:#b7950b"
                                       :                "#d5f5e3;-fx-text-fill:#1e8449";
                        Label badge = new Label(item);
                        badge.setPadding(new Insets(3, 10, 3, 10));
                        badge.setStyle(
                            "-fx-background-color: " + couleur +
                            "; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;"
                        );
                        setGraphic(badge); setText(null);
                        setStyle(
                            "-fx-alignment: center-left; -fx-padding: 0 10;" +
                            "-fx-background-color: transparent;" +
                            "-fx-border-color: transparent;"
                        );
                    }
                }
            }
        });

        tableauActivites.getColumns().addAll(colNom, colHoraire, colPlaces);
        tableauActivites.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rafraichirActivites();

        Button btnInscrire = new Button("✅  S'inscrire");
        btnInscrire.setStyle(styleBoutonPrincipal);
        btnInscrire.setOnAction(e -> sInscrire());

        HBox barre = new HBox(btnInscrire);
        barre.setAlignment(Pos.CENTER_RIGHT);
        barre.setPadding(new Insets(10, 0, 0, 0));

        VBox panneau = new VBox(10, tableauActivites, barre);
        panneau.setPadding(new Insets(15));
        panneau.setStyle("-fx-background-color: #f4f6f8;");
        VBox.setVgrow(tableauActivites, Priority.ALWAYS);
        return panneau;
    }

    // ===================== PANNEAU MES INSCRIPTIONS =====================

    private VBox creerPanneauMesInscriptions() {
        tableauMesInscriptions = new TableView<>();
        appliquerStyleTableau(tableauMesInscriptions);

        // ── Row highlight for inscriptions table ───────────────────────────
        tableauMesInscriptions.setRowFactory(tv -> new TableRow<Inscription>() {
            @Override
            protected void updateItem(Inscription inscription, boolean empty) {
                super.updateItem(inscription, empty);
                styleProperty().unbind();
                if (empty || inscription == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    tv.getSelectionModel().selectedItemProperty().addListener(
                        (obs, o, n) -> appliquerStyle(this)
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

        TableColumn<Inscription, String> colActivite = new TableColumn<>("Activité");
        TableColumn<Inscription, String> colStatut   = new TableColumn<>("Statut");
        TableColumn<Inscription, Void>   colCarte    = new TableColumn<>("Carte");

        colActivite.setPrefWidth(250);
        colStatut.setPrefWidth(180);
        colCarte.setPrefWidth(160);

        appliquerStyleEntete(colActivite, colStatut, colCarte);

        colActivite.setCellValueFactory(c -> {
            String nom = "?";
            for (Activite a : activiteDAO.getTous()) {
                if (a.getId() == c.getValue().getActiviteId()) { nom = a.getNom(); break; }
            }
            return new SimpleStringProperty(nom);
        });

        colStatut.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getStatut().toString())
        );

        colActivite.setCellFactory(col -> celluleStyleeInscription());

        // Statut badge: transparent bg when selected so row color shows through
        colStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    boolean selected = getTableRow() != null && getTableRow().isSelected();
                    if (selected) {
                        setText(item); setGraphic(null);
                        setStyle(
                            "-fx-padding: 0 10; -fx-font-size: 13px;" +
                            "-fx-text-fill: #1a5276;" +
                            "-fx-background-color: transparent;"
                        );
                    } else {
                        Label badge = new Label(item);
                        badge.setPadding(new Insets(3, 10, 3, 10));
                        badge.setStyle(getBadgeStyle(item));
                        setGraphic(badge); setText(null);
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
                    case "ACCEPTEE" -> "-fx-background-color: #d5f5e3; -fx-text-fill: #1e8449; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;";
                    case "REFUSEE"  -> "-fx-background-color: #fadbd8; -fx-text-fill: #c0392b; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;";
                    default         -> "-fx-background-color: #fef9e7; -fx-text-fill: #b7950b; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;";
                };
            }
        });

        // Carte column: adapt bg to row selection state
        colCarte.setCellFactory(col -> new TableCell<>() {
            private final Button btnTelecharger = new Button("⬇  Télécharger");
            {
                btnTelecharger.setStyle(
                    "-fx-background-color: #27ae60; -fx-text-fill: white;" +
                    "-fx-font-size: 11px; -fx-padding: 4 10; -fx-background-radius: 6; -fx-cursor: hand;"
                );
                btnTelecharger.setOnAction(e -> {
                    Inscription inscription = getTableView().getItems().get(getIndex());
                    Activite activite = null;
                    for (Activite a : activiteDAO.getTous()) {
                        if (a.getId() == inscription.getActiviteId()) { activite = a; break; }
                    }
                    if (activite != null) {
                        String chemin = cardpdf.generer(membre, activite);
                        new Alert(Alert.AlertType.INFORMATION, "Carte téléchargée !\n" + chemin, ButtonType.OK).showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    Inscription inscription = getTableView().getItems().get(getIndex());
                    // Transparent so the row highlight color shows through
                    setStyle(
                        "-fx-alignment: center-left; -fx-padding: 0 10;" +
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;"
                    );
                    setGraphic(inscription.getStatut() == Inscription.Statut.ACCEPTEE ? btnTelecharger : null);
                }
            }
        });

        tableauMesInscriptions.getColumns().addAll(colActivite, colStatut, colCarte);
        tableauMesInscriptions.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rafraichirMesInscriptions();

        Button btnAnnuler = new Button("❌  Annuler mon inscription");
        btnAnnuler.setStyle(styleBoutonDanger);
        btnAnnuler.setOnAction(e -> annulerInscription());

        HBox barre = new HBox(btnAnnuler);
        barre.setAlignment(Pos.CENTER_RIGHT);
        barre.setPadding(new Insets(10, 0, 0, 0));

        VBox panneau = new VBox(10, tableauMesInscriptions, barre);
        panneau.setPadding(new Insets(15));
        panneau.setStyle("-fx-background-color: #f4f6f8;");
        VBox.setVgrow(tableauMesInscriptions, Priority.ALWAYS);
        return panneau;
    }

    // ===================== UTILITAIRES =====================

    private void appliquerStyleTableau(TableView<?> tv) {
        tv.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: transparent;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );
        tv.setFixedCellSize(42);
    }

    private void appliquerStyleEntete(TableColumn<?, ?>... colonnes) {
        for (TableColumn<?, ?> col : colonnes) {
            Label label = new Label(col.getText());
            label.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
            );
            col.setGraphic(label);
            col.setText("");
            col.setStyle("-fx-background-color: #2c3e50; -fx-padding: 10 0;");
        }
    }

    private Label creerEntete(String texte) {
        Label label = new Label(texte);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        return label;
    }

    // ── Cell: transparent bg so row highlight shows through ────────────────
    private TableCell<Activite, String> celluleStyleeActivite() {
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
                        "-fx-padding: 0 10; -fx-font-size: 13px;" +
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + (selected ? "#1a5276" : "#2c3e50") + ";" +
                        "-fx-border-color: transparent transparent #ecf0f1 transparent;" +
                        "-fx-border-width: 0 0 1 0;"
                    );
                }
            }
        };
    }

    private TableCell<Inscription, String> celluleStyleeInscription() {
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
                        "-fx-padding: 0 10; -fx-font-size: 13px;" +
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + (selected ? "#1a5276" : "#2c3e50") + ";" +
                        "-fx-border-color: transparent transparent #ecf0f1 transparent;" +
                        "-fx-border-width: 0 0 1 0;"
                    );
                }
            }
        };
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