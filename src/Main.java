import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Créer le dossier data s'il n'existe pas encore
        new File("data").mkdirs();

        // On démarre toujours par la fenêtre de login
        new LoginView().afficher(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
