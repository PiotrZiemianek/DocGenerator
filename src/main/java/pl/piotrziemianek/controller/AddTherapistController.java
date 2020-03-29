package pl.piotrziemianek.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pl.piotrziemianek.dao.TherapistDao;
import pl.piotrziemianek.domain.Therapist;
import pl.piotrziemianek.service.FXMLLoaderContainer;

public class AddTherapistController {

    @FXML
    private TextField academicDegreeTF;

    @FXML
    private TextField therapistNameTF;

    @FXML
    private TextField therapistSurnameTF;

    @FXML
    private Button addButton;

    TherapistDao therapistDao = new TherapistDao();


    public void initialize() {
        setTextFieldListener(therapistNameTF, therapistSurnameTF);

        setTextFieldListener(therapistSurnameTF, therapistNameTF);

        addButton.setDisable(true);
        addButton.setOnAction(event -> {
            Therapist therapist = createTherapist();
            int therapistId = therapistDao.create(therapist);
            TherapistController therapistController = FXMLLoaderContainer.getSelectionWindowLoader().getController();
            ComboBox<Therapist> therapistsBox = therapistController.getTherapistsBox();
            if (therapistId != -1) {
                therapistsBox.getItems().add(therapist);
                therapistsBox.getSelectionModel().select(therapist);
            }
            Stage window = (Stage) addButton.getScene().getWindow();
            window.close();
        });
    }

    /**
     * Method activate addButton when both text fields ale not blank.
     *
     * @param textField        method add listener for it
     * @param textFieldToCheck check is blank
     */
    private void setTextFieldListener(TextField textField, TextField textFieldToCheck) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBlank() || textFieldToCheck.getText().isBlank()) {
                addButton.setDisable(true);
            } else {
                addButton.setDisable(false);
            }
        });
    }

    private Therapist createTherapist() {
        return Therapist.builder().academicDegree(academicDegreeTF.getText())
                .firstName(therapistNameTF.getText())
                .lastName(therapistSurnameTF.getText())
                .build();
    }

}