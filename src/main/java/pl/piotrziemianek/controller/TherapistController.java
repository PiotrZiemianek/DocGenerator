package pl.piotrziemianek.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.piotrziemianek.dao.PatientDao;
import pl.piotrziemianek.dao.TherapistDao;
import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.Therapist;
import pl.piotrziemianek.service.FXMLLoaderContainer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TherapistController {
    @FXML
    private Button delTherapistButton;

    @FXML
    private Button newTherapistButton;

    @FXML
    private ComboBox<Therapist> therapistsBox;

    @FXML
    private Button delPatientButton;

    @FXML
    private Button newPatientButton;

    @FXML
    private ComboBox<Patient> patientsBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ListView<LocalDate> dateList;

    @FXML
    private Button nextStageButton;

    private Parent addTherapist;

    {
        try {
            addTherapist = FXMLLoaderContainer.getAddTherapistPopupLoader().load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Scene addTherapistScene = new Scene(addTherapist);

    private Parent main;

    {
        try {
            main = FXMLLoaderContainer.getMainWindowLoader().load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //other
    private String pattern = "dd.MM.yyyy";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    private TherapistDao therapistDao = new TherapistDao();
    private PatientDao patientDao = new PatientDao();


    public void initialize() {

        newTherapistButton.setOnAction(e -> {
            showNewWindow(newTherapistButton.getScene().getWindow(), addTherapistScene, "Dodaj terapeutę");
        });

        setDelTherapistButton();

        newPatientButton.setDisable(true);

        delPatientButton.setDisable(true);

        newPatientButton.setDisable(true);

        setTherapistsBox();

        setPatientsBox();

        setDatePicker();

        setDateList();
    }

    private void setDelTherapistButton() {
        delTherapistButton.setDisable(true);
        delTherapistButton.setOnAction(event -> {
            Therapist selectedItem = therapistsBox.getSelectionModel().getSelectedItem();
            boolean deleted = therapistDao.delete(selectedItem.getId());
            if (deleted) {
                therapistsBox.getItems().remove(selectedItem);
            }
        });
    }

    private void showNewWindow(Window primaryStage, Scene scene, String title) {


        // New window (Stage)
        Stage newWindow = new Stage();

        newWindow.setTitle(title);
        newWindow.setScene(scene);

        // Specifies the modality for new window.
        newWindow.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindow.initOwner(primaryStage);
        newWindow.setResizable(false);

        // Set position of second window, related to primary window.
//        newWindow.setX(primaryStage.getX() + 200);
//        newWindow.setY(primaryStage.getY() + 100);

        newWindow.show();
    }

    private void setTherapistsBox() {
        therapistsBox.getItems().addAll(therapistDao.findAll());
        therapistsBox.setPromptText("Wybierz terapeutę");
        therapistsBox.setOnAction(event -> {
            Therapist selectedItem = therapistsBox.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                delTherapistButton.setDisable(false);
                newPatientButton.setDisable(false);
                patientsBox.setDisable(false);
                patientsBox.getItems().clear();
                patientsBox.getItems().addAll(selectedItem.getPatients());
            } else {
                delTherapistButton.setDisable(true);
                newPatientButton.setDisable(true);
                patientsBox.setDisable(true);
                patientsBox.getItems().clear();
            }
           setDisableForNextStageButton();
        });
    }

    private void setPatientsBox() {
        patientsBox.setDisable(true);
        patientsBox.getItems().addAll(patientDao.findAll());
        patientsBox.setPromptText("Wybierz pacjenta");
        patientsBox.setOnAction(event -> {
            Patient selectedItem = patientsBox.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                delPatientButton.setDisable(false);
            } else {
                delPatientButton.setDisable(true);
            }
            setDisableForNextStageButton();
        });
    }

    private void setDateList() {
        dateList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(dateFormatter));
                }
            }
        });
        dateList.setOnKeyPressed(event ->

        {
            if (event.getCode() == KeyCode.DELETE) {
                dateList.getItems().removeAll(dateList.getSelectionModel().getSelectedItems());
            }
        });
        dateList.getSelectionModel().

                setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void setDatePicker() {
        datePicker.setPromptText(pattern);
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date == null) ? "" : dateFormatter.format(date);
            }

            @Override
            public LocalDate fromString(String string) {
                return ((string == null) || string.isEmpty()) ? null : LocalDate.parse(string, dateFormatter);
            }
        });

        datePicker.setOnAction(e -> {
            LocalDate value = datePicker.getValue();
            if (dateList.getItems().contains(value)) {
                dateList.getItems().remove(value);
            } else {
                dateList.getItems().add(value);
            }
        });

        datePicker.setDayCellFactory(new Callback<>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        boolean alreadySelected = dateList.getItems().contains(item);

                        setStyle(alreadySelected ? "-fx-background-color: #09a30f;" : "-fx-background-color: -fx-background;");
                        if (dateList.getItems().isEmpty()) {
                            setDisable(false);
                        } else {
                            LocalDate minDate = dateList.getItems().stream().findFirst().get().withDayOfMonth(1);
                            LocalDate maxDate = minDate.withDayOfMonth(minDate.lengthOfMonth());
                            if (item.isBefore(minDate)) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            } else if (item.isAfter(maxDate)) {
                                setDisable(true);
                                setStyle("-fx-background-color: #ffc0cb;");
                            }

                        }
                    }
                };
            }
        });
    }

    private void setDisableForNextStageButton() {
        boolean isTherapistAndPatientSelected = patientsBox.getSelectionModel().getSelectedItem() != null
                && therapistsBox.getSelectionModel().getSelectedItem() != null;
        if (isTherapistAndPatientSelected) {
            nextStageButton.setDisable(false);
        } else {
            nextStageButton.setDisable(true);
        }
    }

    public ComboBox<Therapist> getTherapistsBox() {
        return therapistsBox;
    }
}
