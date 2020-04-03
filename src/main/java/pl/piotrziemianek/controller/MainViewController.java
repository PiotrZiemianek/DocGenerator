package pl.piotrziemianek.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.layout.GridPane;
import pl.piotrziemianek.dao.SubjectDao;
import pl.piotrziemianek.dao.SupportDao;
import pl.piotrziemianek.domain.*;

import java.time.LocalDate;

public class MainViewController {

    @FXML
    private GridPane therapiesGP;

    @FXML
    private Label dateLabel;

    @FXML
    private ComboBox<Subject> subjectsCB;

    @FXML
    private ComboBox<Support> supportsCB;

    @FXML
    private ListView<TherapiesCard> cardsHistoryLV;

    @FXML
    private SplitMenuButton generateBut;

    @FXML
    private MenuItem pdfMI;

    @FXML
    private MenuItem docxMI;



    private SubjectDao subjectDao = new SubjectDao();
    private SupportDao supportDao = new SupportDao();

    public void initialize() {
        subjectsCB.setItems(FXCollections.observableArrayList(subjectDao.findAll()));
        supportsCB.setItems(FXCollections.observableArrayList(supportDao.findAll()));
    }

    protected void setup(Therapist therapist, Patient patient, ObservableList<LocalDate> therapiesDates) {
        therapistLabel.setText(therapist.toString());
        patientsCB.setItems(FXCollections.observableArrayList(therapist.getPatients()));
        cardsHistoryLV.setItems(FXCollections.observableArrayList(therapist.getTherapiesCardList()));
        patientsCB.getSelectionModel().select(patient);
    }
}