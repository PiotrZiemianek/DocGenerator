package pl.piotrziemianek.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.piotrziemianek.dao.PatientDao;
import pl.piotrziemianek.dao.TherapistDao;
import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.Therapist;
import pl.piotrziemianek.service.FXMLLoaderContainer;

public class AddPatientController {


    @FXML
    private Label therapistNameLabel;

    @FXML
    private Button assignToTherapistButton;

    @FXML
    private TextField patientNameTF;

    @FXML
    private TextField patientSurnameTF;

    @FXML
    private Button addButton;

    @FXML
    private ListView<Patient> availablePatientsLV;

    private PatientDao patientDao = new PatientDao();

    private TherapistDao therapistDao = new TherapistDao();

    private Therapist therapist;


    public void initialize() {
        availablePatientsLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availablePatientsLV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Patient> selectedPatients = availablePatientsLV.getSelectionModel().getSelectedItems();
            if (selectedPatients.isEmpty()) {
                assignToTherapistButton.setDisable(true);
            } else {
                assignToTherapistButton.setDisable(false);
            }
        });

        assignToTherapistButton.setDisable(true);
        assignToTherapistButton.setOnAction(event -> {
            SelectionWindowController selectionWindowController = FXMLLoaderContainer.getSelectionWindowLoader().getController();
            ObservableList<Patient> selectedPatients = availablePatientsLV.getSelectionModel().getSelectedItems();
            selectedPatients.forEach(therapist::addPatient);
            therapistDao.update(therapist);
            selectionWindowController.getTherapistsBox()
                    .setItems(FXCollections.observableArrayList(therapistDao.findAll()));
            selectionWindowController.getTherapistsBox().getSelectionModel().select(therapist);
//            selectionWindowController.getPatientsBox()
//                    .setItems(FXCollections.observableArrayList(therapist.getPatients())); //it does when therapist is selected
            Stage window = (Stage) addButton.getScene().getWindow();
            window.close();
        });

        setTextFieldListener(patientNameTF, patientSurnameTF);

        setTextFieldListener(patientSurnameTF, patientNameTF);

        addButton.setDisable(true);
        addButton.setOnAction(event -> {
            Patient patient = createPatient();
            patient.addTherapist(therapist);
            int patientId = patientDao.create(patient);
            SelectionWindowController selectionWindowController = FXMLLoaderContainer.getSelectionWindowLoader().getController();
            ComboBox<Patient> patientsBox = selectionWindowController.getPatientsBox();
            if (patientId != -1) {
                patientsBox.getItems().add(patient);
                patientsBox.getSelectionModel().select(patient);
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

    private Patient createPatient() {
        return Patient.builder()
                .firstName(patientNameTF.getText())
                .lastName(patientSurnameTF.getText())
                .build();
    }

    protected void setup(Therapist therapist) {
        patientNameTF.clear();
        patientSurnameTF.clear();
        availablePatientsLV.getItems().clear();
        availablePatientsLV.getItems().addAll(patientDao.findAll());
        therapistNameLabel.setText(therapist.toString());
        if (therapist.getPatients() != null) availablePatientsLV.getItems().removeAll(therapist.getPatients());
        this.therapist = therapist;
    }

}
