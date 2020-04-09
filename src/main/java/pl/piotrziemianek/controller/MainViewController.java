package pl.piotrziemianek.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pl.piotrziemianek.dao.*;
import pl.piotrziemianek.domain.*;
import pl.piotrziemianek.service.DocCreator;
import pl.piotrziemianek.util.FXMLLoaderContainer;
import pl.piotrziemianek.util.AutoCompleteBox;
import pl.piotrziemianek.util.PreTCard;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

import static javafx.scene.control.Alert.*;
import static javafx.scene.control.ButtonBar.*;

public class MainViewController {

    @FXML
    private CheckBox autoCompleteCB;

    @FXML
    private Button openCardFromHistoryBut;

    @FXML
    private TableView<Therapy> therapiesTable;

    @FXML
    private TableColumn<Therapy, LocalDate> therapyDateColl;

    @FXML
    private TableColumn<Therapy, Set<Subject>> therapySubColl;

    @FXML
    private TableColumn<Therapy, Set<Support>> therapySupColl;

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
    private TherapiesCardDao therapiesCardDao = new TherapiesCardDao();
    private PreTCard preTCard;

    public void initialize() {
        therapyCardGroup.setDisable(true);

        docxMI.setOnAction(event -> {
            generateBut.setText("Generuj (DOCX)");
            generateBut.setOnAction(event1 -> {
                TherapiesCard therapiesCard = preTCard.getTherapiesCard();
                therapiesCard.setTherapies(therapiesTable.getItems());
                Patient patient = preTCard.getPatient();
                Therapist therapist = preTCard.getTherapist();
                if (therapiesCard.getPatient() == null) {
                    therapiesCard.setPatient(patient);
                }

                if (therapiesCard.getTherapist() == null) {
                    therapiesCard.setTherapist(therapist);
                }
//                int i = therapiesCardDao.createOrUpdate(therapiesCard);
//                if (i == -1) {
//                    //todo revert
//                }
                DocCreator docCreator = new DocCreator(therapiesCard);

                String docxPath = docCreator.createDocx();
                showDocument(docxPath);

            });
        });
        pdfMI.setOnAction(event -> {
            generateBut.setText("Generuj (PDF)");
//            generateBut.setOnAction(event1 -> ); //todo
        });
        pdfMI.fire();

        setupOpenCardFromHistoryBut();

        cardsHistoryLV.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openCardFromHistoryBut.setDisable(false);
            } else {
                openCardFromHistoryBut.setDisable(true);
            }
        });

        setupCreateTherapiesCardBut();

        setupDelTherapistButton();

        setupNewTherapistButton();

        setupDelPatientButton();

        setupNewPatientButton();

        setupTherapistsBox();

        setupPatientsBox();

        setupDatePicker();

        setupDateList();

    }

    private void showDocument(String docPath) {
        try {

            File document = new File(docPath);
            if (document.exists()) {

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(document);
                } else {
                    System.out.println("Awt Desktop is not supported!");
                }

            } else {
                System.out.println("File is not exists!");
            }
            System.out.println("Done");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setupOpenCardFromHistoryBut() {
        openCardFromHistoryBut.setDisable(true);
        openCardFromHistoryBut.setOnAction(event -> {
            therapyCardGroup.setDisable(false);
            autoCompleteCB.setVisible(false);
            setTherapiesTable(cardsHistoryLV.getSelectionModel().getSelectedItem());
        });
    }

    private void setupCreateTherapiesCardBut() {
        createTherapiesCard.setDisable(true);
        createTherapiesCard.setOnAction(event -> {
            therapyCardGroup.setDisable(false);
            autoCompleteCB.setVisible(true);

            FXCollections.sort(dateList.getItems());
            TherapiesCard therapiesCard = new TherapiesCard();
            Patient patient = patientsBox.getSelectionModel().getSelectedItem();
            Therapist therapist = therapistsBox.getSelectionModel().getSelectedItem();
            for (LocalDate therapyDate : dateList.getItems()) {
                therapiesCard.addTherapy(new Therapy(therapyDate));
            }
            therapiesCard.setYearMonth(therapiesCard.getTherapies().stream()
                    .findFirst()
                    .orElse(new Therapy())
                    .getTherapyDate());

            preTCard = new PreTCard(therapist, patient, therapiesCard);

            setTherapiesTable(therapiesCard);
        });
    }

    private void setTherapiesTable(TherapiesCard therapiesCard) {
        therapiesTable.setItems(FXCollections.observableArrayList(therapiesCard.getTherapies()));
        therapiesTable.setFixedCellSize(150);

        therapyDateColl.setCellValueFactory(new PropertyValueFactory<>("therapyDate"));
        therapyDateColl.setCellFactory(param -> new TableCell<Therapy, LocalDate>() {
            @Override
            public void updateItem(LocalDate localDate, boolean empty) {
                super.updateItem(localDate, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(localDate.format(dateFormatter));
                }
            }
        });

        //Subjects cell
        therapySubColl.setCellValueFactory(new PropertyValueFactory<>("subjects"));
        therapySubColl.setCellFactory(col -> {
            ListView<Subject> listView = new ListView<>();
            setupCellWrappingWidth(listView);
            setLVEditableOnMouseDoubleClick(listView, subjectDao, this::setupNewDelSubject);
            return getTableCellAsListView(listView);
        });

        //Supports cell
        therapySupColl.setCellValueFactory(new PropertyValueFactory<>("supports"));
        therapySupColl.setCellFactory(col -> {
            ListView<Support> listView = new ListView<>();
            setupCellWrappingWidth(listView);
            setLVEditableOnMouseDoubleClick(listView, supportDao, this::setupNewDelSupport);
            return getTableCellAsListView(listView);
        });
    }

    private <T> void setupCellWrappingWidth(ListView<T> listView) {
        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<T> call(ListView<T> list) {

                return new ListCell<>() {
                    @Override
                    public void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            Text text = new Text(item.toString());
                            text.setTextAlignment(TextAlignment.CENTER);
                            text.setWrappingWidth(215);
                            setGraphic(text);
                            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }

    private <T> TableCell<Therapy, Set<T>> getTableCellAsListView(ListView<T> listView) {
        return new TableCell<Therapy, Set<T>>() {
            @Override
            public void updateItem(Set<T> items, boolean empty) {
                super.updateItem(items, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    listView.getItems().setAll(items);
                    ObservableList<T> listViewItems = listView.getItems();
                    listViewItems.addListener((Change<? extends T> c) -> {
                        while (c.next()) {
                            if (c.wasAdded()) {
                                items.addAll(c.getAddedSubList());
                            }
                            if (c.wasRemoved()) {
                                items.removeAll(c.getRemoved());
                            }
                        }
                    });
                    listView.setMaxSize(300, 300);
                    setGraphic(listView);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        };
    }

    private <T> void setLVEditableOnMouseDoubleClick(ListView<T> listView, CrudAccessible<T> dao, BiConsumer<Button[], ListView<T>> setupNewDelButtons) {
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2 && autoCompleteCB.isVisible()) {
                    Dialog<List<T>> dialog = new Dialog<>();
                    dialog.setTitle("Dodaj lub usuń");
                    dialog.setHeaderText(null);

                    ButtonType okButtonType = new ButtonType("Zatwierdź", ButtonData.OK_DONE);
                    ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonData.CANCEL_CLOSE);


                    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

                    GridPane grid = new GridPane();
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(10, 10, 10, 10));


                    ListView<T> allItemsList = new ListView<>();
                    allItemsList.getItems().setAll(dao.findAll());
                    setupMultipleSelectionModelOnMouseClickWithoutCtrl(allItemsList);
                    MultipleSelectionModel<T> selectionModel = allItemsList.getSelectionModel();

                    listView.getItems().stream()
                            .filter(t -> allItemsList.getItems().contains(t))
                            .forEach(selectionModel::select);

                    GridPane innerGrid = new GridPane();

                    Button newItemBut = new Button("Nowy");
                    Button delItemBut = new Button("Usuń");
                    setupNewDelButtons.accept(new Button[]{newItemBut, delItemBut}, allItemsList);

                    innerGrid.add(newItemBut, 0, 0);
                    innerGrid.add(delItemBut, 1, 0);

                    grid.add(innerGrid, 0, 0);
                    grid.add(allItemsList, 0, 1);

                    dialog.getDialogPane().setContent(grid);

                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == okButtonType) {
                            return selectionModel.getSelectedItems();
                        }
                        return null;
                    });
                    Optional<List<T>> result = dialog.showAndWait();

                    if (result.isPresent()) {
                        listView.getItems().setAll(result.get());
                    } else {
                        listView.getItems().clear();
                    }
                }
            }

            private void setupMultipleSelectionModelOnMouseClickWithoutCtrl(ListView<T> allItemsList) {
                MultipleSelectionModel<T> selectionModel = allItemsList.getSelectionModel();
                selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
                allItemsList.setCellFactory(lv -> {
                    ListCell<T> cell = new ListCell<>() {
                        @Override
                        public void updateItem(T item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!isEmpty()) {
                                Text text = new Text(item.toString());
                                text.setTextAlignment(TextAlignment.CENTER);
                                text.setWrappingWidth(230);
                                setGraphic(text);
                                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                            } else {
                                setGraphic(null);
                            }
                        }
                    };

//                    cell.itemProperty().addListener((observable, oldValue, newValue) -> {
//
//                        cell.textProperty().bind(Bindings
//                                .when(cell.emptyProperty())
//                                .then("")
//                                .otherwise(cell.itemProperty().asString()));
//                    });

                    cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                        allItemsList.requestFocus();
                        if (!cell.isEmpty()) {
                            int index = cell.getIndex();
                            if (selectionModel.getSelectedIndices().contains(index)) {
                                selectionModel.clearSelection(index);
                            } else {
                                selectionModel.select(index);
                            }
                            event.consume();
                        }
                    });
                    return cell;
                });
            }
        });
    }

    private void setupNewDelSupport(Button[] newDelButtons, ListView listView) {
        Button newButton = newDelButtons[0];
        Button delButton = newDelButtons[1];
        newButton.setOnAction(event -> {
            TextInputDialog newItemDial = new TextInputDialog();
            newItemDial.setTitle("Wrowadź nowe wspomaganie");
            newItemDial.setHeaderText("Wprowadź nowe wspomaganie");
            newItemDial.setContentText(null);

            Optional<String> result = newItemDial.showAndWait();
            result.ifPresent(s -> {
                String sup = result.get();
                if (!sup.isBlank()) {
                    Support support = new Support(sup);
                    int i = supportDao.create(support);

                    if (i != -1) {
                        ((ListView<Support>) listView).getItems().add(support);
                    } else {
                        saveFailedAlert("Upewnij się czy nie istnieje już takie wspomaganie w systemie.");
                    }
                } else {
                    saveFailedAlert("Wprowadź treść wspomagania przed zapisem");
                }
            });
        });
        delButton.setOnAction(event -> {
            ChoiceDialog<Support> delCB = new ChoiceDialog<>();
            delCB.getItems().setAll(((ListView<Support>) listView).getItems());
            delCB.setTitle("Usuwanie");
            delCB.setHeaderText("Które wspomaganie usunąć?");
            delCB.setContentText(null);

            Optional<Support> result = delCB.showAndWait();

            result.ifPresent(support -> {
                boolean delete = supportDao.delete(support.getId());
                if (delete) {
                    listView.getItems().remove(support);
                } else {
                    deleteFailedAlert("Z tego wspomagania korzystają inne karty terapii.");
                }
            });
        });
    }

    private void setupNewDelSubject(Button[] newDelButtons, ListView listView) {
        Button newButton = newDelButtons[0];
        Button delButton = newDelButtons[1];
        newButton.setOnAction(event -> {
            TextInputDialog newItemDial = new TextInputDialog();
            newItemDial.setTitle("Wrowadź nowy temat zajęć");
            newItemDial.setHeaderText("Wrowadź nowy temat zajęć");
            newItemDial.setContentText(null);

            Optional<String> result = newItemDial.showAndWait();
            result.ifPresent(s -> {
                String sup = result.get();
                if (!sup.isBlank()) {
                    Subject subject = new Subject(sup);
                    int i = subjectDao.create(subject);

                    if (i != -1) {
                        ((ListView<Subject>) listView).getItems().add(subject);
                    } else {
                        saveFailedAlert("Upewnij się czy nie istnieje już taki temat zajęć w systemie.");
                    }
                } else {
                    saveFailedAlert("Wprowadź treść tematu zajęć przed zapisem");
                }
            });
        });
        delButton.setOnAction(event -> {
            ChoiceDialog<Subject> delCB = new ChoiceDialog<>();
            delCB.getItems().setAll(((ListView<Subject>) listView).getItems());
            delCB.setTitle("Usuwanie");
            delCB.setHeaderText("Który temat zajęć usunąć?");
            delCB.setContentText(null);

            Optional<Subject> result = delCB.showAndWait();

            result.ifPresent(subject -> {
                boolean delete = subjectDao.delete(subject.getId());
                if (delete) {
                    listView.getItems().remove(subject);
                } else {
                    deleteFailedAlert("Nie można usunąć tematu zajęć z którego korzystają jakieś karty terapii.");
                }
            });
        });
    }

    private void deleteFailedAlert(String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Nie można usunąć");
        alert.setHeaderText("Usuwanie nie powiodło się");
        Text text = new Text(content);
        text.setWrappingWidth(50);
        alert.getDialogPane().setContent(text);

        alert.showAndWait();
    }

    private void saveFailedAlert(String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Błąd zapisu");
        alert.setHeaderText("Nie zapisano");
        Text text = new Text(content);
        text.setWrappingWidth(50);
        alert.getDialogPane().setContent(text);

        alert.showAndWait();
    }

    private <T> ListView<T> getCellListView() {
        ListView<T> listView = new ListView<>();
        listView.setMaxHeight(100);

        listView.setCellFactory(lv -> new ListCell<T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        return listView;
    }

    private void setupNewTherapistButton() {
        newTherapistButton.setOnAction(e -> {
            AddTherapistController addTherapistController = FXMLLoaderContainer.getAddTherapistPopupLoader().getController();
            addTherapistController.cleanup();
            showNewWindow(newTherapistButton.getScene().getWindow(), addTherapistScene, "Dodaj terapeutę");
        });
    }

    private void setupNewPatientButton() {
        newPatientButton.setOnAction(e -> {
            AddPatientController addPatientController = FXMLLoaderContainer.getAddPatientPopupLoader().getController();
            addPatientController.setup(therapistsBox.getSelectionModel().getSelectedItem());
            showNewWindow(newPatientButton.getScene().getWindow(), addPatientScene, "Dodaj pacjenta");
        });
        newPatientButton.setDisable(true);
    }

    private void setupDelPatientButton() {
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

    private void setupDelTherapistButton() {
        delTherapistButton.setDisable(true);
        delTherapistButton.setOnAction(event -> {
            Therapist selectedItem = therapistsBox.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(AlertType.WARNING);
            alert.getDialogPane().setMaxWidth(250);
            alert.setTitle("Usuń terapeutę");
            alert.setHeaderText("Usunąć terapeutę?");
            alert.setContentText("Terapeuta zostanie usunięty z systemu.");

            ButtonType yesButton = new ButtonType("Tak", ButtonData.NEXT_FORWARD);
            ButtonType noButton = new ButtonType("Nie", ButtonData.CANCEL_CLOSE);

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

    private void setupTherapistsBox() {
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

    private void setupPatientsBox() {
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

    private void setupDateList() {
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

    private void setupDatePicker() {
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