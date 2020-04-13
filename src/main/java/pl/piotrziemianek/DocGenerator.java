package pl.piotrziemianek;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.piotrziemianek.util.FXMLLoaderContainer;

import java.io.IOException;

public class DocGenerator extends Application {

    private FXMLLoader mainViewLoader = FXMLLoaderContainer.getMainViewLoader();
    private Parent mainView = mainViewLoader.load();

    public DocGenerator() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(mainView));
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.setTitle("DocGenerator");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

//    @Override
//    public void stop() {
//        MainViewController controller = mainFxmlLoader.getController();
//        //ex. save data to db
//    }



    public static void main(String[] args) {
        launch(args);
    }
}
