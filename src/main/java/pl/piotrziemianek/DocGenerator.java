package pl.piotrziemianek;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.piotrziemianek.controller.FXController;

import java.io.IOException;

public class DocGenerator extends Application {

    private FXMLLoader mainFxmlLoader = new FXMLLoader(getClass().getResource("/view/main_view.fxml"));
    private Parent root = mainFxmlLoader.load();

    public DocGenerator() throws IOException {
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(root));
        stage.show();
        showNewWindow(stage);
    }

//    @Override
//    public void stop() {
//        FXController controller = mainFxmlLoader.getController();
//        //ex. save data to db
//    }

    public void showNewWindow(Window primaryStage) {

        Label secondLabel = new Label("I'm a Label on new Window");

        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);

        Scene secondScene = new Scene(secondaryLayout, 230, 100);

        // New window (Stage)
        Stage newWindow = new Stage();
        newWindow.setTitle("Second Stage");
        newWindow.setScene(secondScene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        newWindow.setX(primaryStage.getX() + 200);
        newWindow.setY(primaryStage.getY() + 100);

        newWindow.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
