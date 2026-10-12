package ni.uam.edu.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("procesador-view.fxml"));
        Scene scene = new Scene(loader.load(), 760, 620);
        stage.setTitle("JavaFX - Monitor de procesamiento concurrente");
        stage.setMinWidth(680);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();

        ProcesadorController controller = loader.getController();
        stage.setOnCloseRequest(event -> controller.detenerTodosAlCerrar());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
