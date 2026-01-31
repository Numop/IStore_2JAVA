package IStore;

import IStore.dao.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager.getInstance();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/IStore/view/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 550);

        primaryStage.setTitle("iStore - Connexion");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    @Override
    public void stop() {
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}