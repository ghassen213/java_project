package view;

import controller.LoginController;
import model.Membre;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView {

    private LoginController loginController = new LoginController();

    public void afficher(Stage stage) {
        stage.setTitle("Club Sportif");
        stage.setWidth(700);
        stage.setHeight(600);

        // --- Titre ---
        Label titre = new Label("Club Sportif");
        titre.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label sousTitre = new Label("Connectez-vous à votre espace");
        sousTitre.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        // --- Champs ---
        TextField champLogin = new TextField();
        champLogin.setPromptText("Login");
        champLogin.setStyle("-fx-font-size: 13px; -fx-padding: 8px; -fx-background-radius: 6;");

        PasswordField champMdp = new PasswordField();
        champMdp.setPromptText("Mot de passe");
        champMdp.setStyle("-fx-font-size: 13px; -fx-padding: 8px; -fx-background-radius: 6;");

        // --- Bouton ---
        Button btnConnexion = new Button("Se connecter");
        btnConnexion.setMaxWidth(Double.MAX_VALUE);
        btnConnexion.setStyle(
            "-fx-background-color: #2c3e50; -fx-text-fill: white;" +
            "-fx-font-size: 13px; -fx-padding: 10px; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        // --- Message erreur ---
        Label messageErreur = new Label();
        messageErreur.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

        // --- Action bouton ---
        btnConnexion.setOnAction(e -> {
            String login = champLogin.getText().trim();
            String mdp   = champMdp.getText();

            if (login.isEmpty() || mdp.isEmpty()) {
                messageErreur.setText("⚠ Veuillez remplir tous les champs.");
                return;
            }

            if (loginController.estAdmin(login, mdp)) {
                new DashboardAdminView().afficher(stage);
                return;
            }

            Membre membre = loginController.connecterMembre(login, mdp);
            if (membre != null) {
                if (membre.isPremierAcces()) {
                    new ChangerMotDePasseView().afficher(stage, membre);
                } else {
                    new EspaceMemberView().afficher(stage, membre);
                }
            } else {
                messageErreur.setText("⚠ Login ou mot de passe incorrect.");
            }
        });

        // --- Layout carte centrale ---
        VBox carte = new VBox(12, titre, sousTitre, champLogin, champMdp, btnConnexion, messageErreur);
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setPadding(new Insets(35));
        carte.setMaxWidth(320);
        carte.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 4);"
        );

        // --- Fond coloré ---
        VBox fond = new VBox(carte);
        fond.setAlignment(Pos.CENTER);
        fond.setStyle("-fx-background-color: #ecf0f1;");

        stage.setScene(new Scene(fond, 480, 380));
        stage.show();
    }
}