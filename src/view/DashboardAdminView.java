package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardAdminView {

    public void afficher(Stage stage) {
        stage.setTitle("Dashboard Administrateur");
    
     
        
        // --- Titre ---
        Label titre = new Label("Espace Administrateur");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label sousTitre = new Label("Que voulez-vous gérer ?");
        sousTitre.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        // --- Boutons de navigation ---
        Button btnMembres      = creerBouton("👤  Gérer les membres");
        Button btnActivites    = creerBouton("🏃  Gérer les activités");
        Button btnInscriptions = creerBouton("📋  Gérer les inscriptions");

        // --- Bouton déconnexion (style différent) ---
        Button btnDeconnexion = new Button("Se déconnecter");
        btnDeconnexion.setPrefWidth(220);
        btnDeconnexion.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #e74c3c;" +
            "-fx-border-color: #e74c3c; -fx-border-radius: 6; -fx-background-radius: 6;" +
            "-fx-font-size: 13px; -fx-padding: 8px; -fx-cursor: hand;"
        );

        // --- Actions ---
        btnMembres.setOnAction(e      -> new GestionMembresView().afficher(stage));
        btnActivites.setOnAction(e    -> new GestionActivitesView().afficher(stage));
        btnInscriptions.setOnAction(e -> new GestionInscriptionsView().afficher(stage));
        btnDeconnexion.setOnAction(e  -> new LoginView().afficher(stage));

        // --- Carte centrale ---
        VBox carte = new VBox(14, titre, sousTitre, btnMembres, btnActivites, btnInscriptions, btnDeconnexion);
        carte.setAlignment(Pos.CENTER);
        carte.setPadding(new Insets(35));
        carte.setMaxWidth(400);
        carte.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 4);"
        );

        // --- Fond ---
        VBox fond = new VBox(carte);
        fond.setAlignment(Pos.CENTER);
        fond.setStyle("-fx-background-color: #ecf0f1;");

        stage.setScene(new Scene(fond, 480, 420));
        stage.show();
    }

    // Méthode utilitaire pour créer un bouton avec le même style
    private Button creerBouton(String texte) {
        Button btn = new Button(texte);
        btn.setPrefWidth(220);
        btn.setStyle(
            "-fx-background-color: #2c3e50; -fx-text-fill: white;" +
            "-fx-font-size: 13px; -fx-padding: 10px; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        return btn;
    }
}