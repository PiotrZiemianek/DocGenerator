package pl.piotrziemianek.controller;

import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

import java.util.Optional;


import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RootController {

    @FXML
    private Button button;


    public void initialize() {
        button.setOnAction(this::buttonClick2);
//        popup();
    }

    public void buttonClick2(ActionEvent actionEvent) {
        popup();

    }

    private void popup() {
        Window owner = button.getScene().getWindow();
        TextInputDialog alert = new TextInputDialog();
        alert.setTitle("Rysuj n trójkątów");
        alert.setHeaderText(null);
        alert.setContentText("Ile trójkątów?");
        alert.initOwner(owner);

        final Optional<String> result = alert.showAndWait();
    }
}
