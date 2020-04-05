package pl.piotrziemianek.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.piotrziemianek.dao.PatientDao;
import pl.piotrziemianek.dao.SubjectDao;
import pl.piotrziemianek.dao.SupportDao;
import pl.piotrziemianek.dao.TherapistDao;
import pl.piotrziemianek.domain.*;
import pl.piotrziemianek.service.FXMLLoaderContainer;
import pl.piotrziemianek.util.AutoCompleteBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MainViewController {

    @FXML
    private Button openCardFromHistoryBut;

    @FXML
    private TreeTableView<Therapy> therapiesTable;

    @FXML
    private ListView<TherapiesCard> cardsHistoryLV;

    @FXML
    private SplitMenuButton generateBut;

    @FXML
    private MenuItem pdfMI;

    @FXML
    private MenuItem docxMI;

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
    private Button createTherapiesCard;

    @FXML
    private Group therapyCardGroup;


    private Parent addTherapist;

    {
        try {
            addTherapist = FXMLLoaderContainer.getAddTherapistPopupLoader().load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Scene addTherapistScene = new Scene(addTherapist);

    private Parent addPatient;

    {
        try {
            addPatient = FXMLLoaderContainer.getAddPatientPopupLoader().load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Scene addPatientScene = new Scene(addPatient);

    //other
    private String pattern = "dd.MM.yyyy";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    private TherapistDao therapistDao = new TherapistDao();
    private PatientDao patientDao = new PatientDao();
    private SubjectDao subjectDao = new SubjectDao();
    private SupportDao supportDao = new SupportDao();

    public void initialize() {
        therapyCardGroup.setDisable(true);

        openCardFromHistoryBut.setDisable(true);

        createTherapiesCard.setDisable(true);
        createTherapiesCard.setOnAction(event -> {
            therapyCardGroup.setDisable(false);
            FXCollections.sort(dateList.getItems());
            TherapiesCard therapiesCard = new TherapiesCard();
            therapiesCard.setPatient(patientsBox.getSelectionModel().getSelectedItem());
            therapiesCard.setTherapist(therapistsBox.getSelectionModel().getSelectedItem());
            for (LocalDate therapyDate : dateList.getItems()) {
                therapiesCard.addTherapy(new Therapy(therapyDate));
            }
            therapiesCard.setYearMonth(therapiesCard.getTherapies().stream()
                    .findFirst()
                    .orElse(new Therapy())
                    .getTherapyDate());

            TreeTableColumn<Therapy, ?> therapyDatesColumn = therapiesTable.getColumns().get(0);
            TreeTableColumn<Therapy, ?> therapySubjectsColumn = therapiesTable.getColumns().get(1);
            TreeTableColumn<Therapy, ?> therapySupportsColumn = therapiesTable.getColumns().get(2);
        });

        setDelTherapistButton();

        setNewTherapistButton();

        setDelPatientButton();

        setNewPatientButton();

        setTherapistsBox();

        setPatientsBox();

        setDatePicker();

        setDateList();
    }

    private void setNewTherapistButton() {
        newTherapistButton.setOnAction(e -> {
            AddTherapistController addTherapistController = FXMLLoaderContainer.getAddTherapistPopupLoader().getController();
            addTherapistController.cleanup();
            showNewWindow(newTherapistButton.getScene().getWindow(), addTherapistScene, "Dodaj terapeutę");
        });
    }

    private void setNewPatientButton() {
        newPatientButton.setOnAction(e -> {
            AddPatientController addPatientController = FXMLLoaderContainer.getAddPatientPopupLoader().getController();
            addPatientController.setup(therapistsBox.getSelectionModel().getSelectedItem());
            showNewWindow(newPatientButton.getScene().getWindow(), addPatientScene, "Dodaj pacjenta");
        });
        newPatientButton.setDisable(true);
    }

    private void setDelPatientButton() {
        delPatientButton.setDisable(true);
        delPatientButton.setOnAction(event -> {
            List<String> choices = Arrays.asList("Wypisz od terapeuty", "Usuń z systemu");
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Wypisz od terapeuty", choices);
            dialog.getDialogPane().setMaxWidth(250);
            dialog.setTitle("Usuń pacjenta");
            dialog.setHeaderText("Usunąć pacjenta?");
            dialog.setContentText("Co zrobić?");

            ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(s -> {
                Patient patient = patientsBox.getSelectionModel().getSelectedItem();
                Therapist therapist = therapistsBox.getSelectionModel().getSelectedItem();

                if (s.equals("Wypisz od terapeuty")) {
                    patient.removeTherapist(therapist);
                    int patientId = patientDao.update(patient);
                    if (patientId != -1) {
                        patientsBox.getItems().clear();
                        patientsBox.getItems().addAll(therapist.getPatients());
                    } else {
                        patient.addTherapist(therapist);
                    }
                } else {
                    boolean deleted = patientDao.delete(patient.getId());
                    if (deleted) {
                        patient.getTherapists().forEach(t -> t
                                .getPatients()
                                .remove(patient)); //todo refactor - maybe overload PatientDao.delete method.
                        patientsBox.getItems().clear();
                        patientsBox.getItems().addAll(therapist.getPatients());
                    }
                }
            });
        });
    }

    private void setDelTherapistButton() {
        delTherapistButton.setDisable(true);
        delTherapistButton.setOnAction(event -> {
            Therapist selectedItem = therapistsBox.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.getDialogPane().setMaxWidth(250);
            alert.setTitle("Usuń terapeutę");
            alert.setHeaderText("Usunąć terapeutę?");
            alert.setContentText("Terapeuta zostanie usunięty z systemu.");

            ButtonType yesButton = new ButtonType("Tak", ButtonBar.ButtonData.NEXT_FORWARD);
            ButtonType noButton = new ButtonType("Nie", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == yesButton) {
                boolean deleted = therapistDao.delete(selectedItem.getId());
                if (deleted) {
                    therapistsBox.getItems().remove(selectedItem);
                }
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
                cardsHistoryLV.setItems(FXCollections.observableArrayList(selectedItem.getTherapiesCardList()));
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
        new AutoCompleteBox<>(patientsBox);
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
            createTherapiesCard.setDisable(false);
        } else {
            createTherapiesCard.setDisable(true);
        }
    }

    protected ComboBox<Therapist> getTherapistsBox() {
        return therapistsBox;
    }

    protected ComboBox<Patient> getPatientsBox() {
        return patientsBox;
    }

}