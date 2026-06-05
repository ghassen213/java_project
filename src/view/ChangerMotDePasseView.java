package view;

import controller.MembreController;
import model.Membre;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ChangerMotDePasseView {

    private MembreController membreController = new MembreController();

    public void afficher(Stage stage, Membre membre) {
        stage.setTitle("Premier accès");

        // --- Titre ---
        Label titre = new Label("🔒 Nouveau mot de passe");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label message = new Label("Bienvenue " + membre.getPrenom() + " ! Choisissez un mot de passe pour continuer.");
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        // --- Champs ---
        PasswordField champNouveau = new PasswordField();
        champNouveau.setPromptText("Nouveau mot de passe");
        champNouveau.setStyle("-fx-font-size: 13px; -fx-padding: 8px; -fx-background-radius: 6;");

        PasswordField champConfirm = new PasswordField();
        champConfirm.setPromptText("Confirmer le mot de passe");
        champConfirm.setStyle("-fx-font-size: 13px; -fx-padding: 8px; -fx-background-radius: 6;");

        // --- Erreur ---
        Label messageErreur = new Label();
        messageErreur.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        // --- Bouton ---
        Button btnValider = new Button("Valider");
        btnValider.setMaxWidth(Double.MAX_VALUE);
        btnValider.setStyle(
            "-fx-background-color: #2c3e50; -fx-text-fill: white;" +
            "-fx-font-size: 13px; -fx-padding: 10px; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        // --- Action ---
        btnValider.setOnAction(e -> {
            String nouveau  = champNouveau.getText();
            String confirme = champConfirm.getText();

            if (!nouveau.equals(confirme)) {
                messageErreur.setText("⚠ Les mots de passe ne correspondent pas.");
                return;
            }

            try {
                membreController.changerMotDePasse(membre, nouveau);
                new EspaceMemberView().afficher(stage, membre);
            } catch (Exception ex) {
                messageErreur.setText("⚠ " + ex.getMessage());
            }
        });

        // --- Carte ---
        VBox carte = new VBox(12, titre, message, champNouveau, champConfirm, btnValider, messageErreur);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setPadding(new Insets(35));
        carte.setMaxWidth(320);
        carte.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 4);"
        );

        // --- Fond ---
        VBox fond = new VBox(carte);
        fond.setAlignment(Pos.CENTER);
        fond.setStyle("-fx-background-color: #ecf0f1;");

        stage.setScene(new Scene(fond, 480, 380));
        stage.show();
    }
}