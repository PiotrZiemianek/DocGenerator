package pl.piotrziemianek.service;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.sun.istack.NotNull;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import pl.piotrziemianek.domain.*;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DocCreator {
    private XWPFDocument doc;

    {
        try (InputStream inputStream = getClass().getResourceAsStream("/template.docx")) {
            doc = new XWPFDocument(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TherapiesCard therapiesCard;
    private Patient patient;
    private Therapist therapist;
    private String yearMonth;
    private List<Therapy> therapies;
    private String pattern = "dd.MM.yyyy";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

    public DocCreator(@NotNull TherapiesCard therapiesCard) {
        this.therapiesCard = therapiesCard;
        patient = therapiesCard.getPatient();
        therapist = therapiesCard.getTherapist();
        yearMonth = therapiesCard.getYearMonthString();
        therapies = therapiesCard.getTherapies();
    }

    public String createDocx() {
        XWPFDocument doc = create();
        String path;
        if (patient != null && yearMonth != null) {
            path = patient.getLastName() + YearMonth.from(therapiesCard.getYearMonth()) + ".docx";
        } else {
            path = "karta_terapii" + LocalDate.now() + ".docx";
        }
        try (FileOutputStream outputStream = new FileOutputStream(path)) {
            doc.write(outputStream);
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    public String createPdf() {
        //todo pdf converter
        XWPFDocument doc = create();
        String path;
        if (patient != null && yearMonth != null) {
            path = patient.getLastName() + YearMonth.from(therapiesCard.getYearMonth()) + ".pdf";
        } else {
            path = "karta_terapii" + LocalDate.now() + ".pdf";
        }
        File outputFile = new File(path);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        try {
            doc.write(arrayOutputStream);
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        IConverter converter = LocalConverter.builder().build();

        try (InputStream inputDocxStream = new ByteArrayInputStream(arrayOutputStream.toByteArray());
             OutputStream outputStream = new FileOutputStream(outputFile)) {
            arrayOutputStream.close();
            converter.convert(inputDocxStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            converter.shutDown();
        }
        return path;
    }

    private XWPFDocument create() {


        if (yearMonth != null) {
            doc = replaceText("therapyCardDate", yearMonth);
        } else {
            doc = replaceText("therapyCardDate", "............................");
        }

        if (patient != null) {
            replaceText("childName", patient.toString());
        } else {
            doc = replaceText("childName", "...................................");
        }

        if (therapist != null) {
            replaceText("therapistName", therapist.toString());
        } else {
            doc = replaceText("therapistName", "...................................");
        }
        XWPFTable table = doc.getTableArray(0);
        if (!therapies.isEmpty()) {
            for (Therapy therapy : therapies) {
                XWPFTableRow emptyRow = table.getRow(1);
                CTRow ctrow = null;
                try {
                    ctrow = CTRow.Factory.parse(emptyRow.getCtRow().newInputStream());
                } catch (XmlException | IOException e) {
                    e.printStackTrace();
                }
                XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

                //TherapyDate
                newRow.getCell(0)
                        .getParagraphArray(0).createRun()
                        .setText(therapy.getTherapyDate().format(dateFormatter));

                //Subjects
                XWPFTableCell subjectsCell = newRow.getCell(1);
                if (!therapy.getSubjects().isEmpty()) {
                    for (Subject subject : therapy.getSubjects()) {
                        String sub = subject.getSubject();
                        if (sub.charAt(sub.length() - 1) != '.') sub = sub.concat(".");
                        subjectsCell.addParagraph().createRun().setText(sub);
                    }
                    subjectsCell.removeParagraph(0);
                }

                //Support
                XWPFTableCell supportCell = newRow.getCell(2);
                if (!therapy.getSupports().isEmpty()) {
                    for (Support support : therapy.getSupports()) {
                        String supp = support.getSupport();
                        if (supp.charAt(supp.length() - 1) != '.') supp = supp.concat(".");
                        supportCell.addParagraph().createRun().setText(supp);
                    }
                    supportCell.removeParagraph(0);
                }

                table.addRow(newRow);
            }
            table.removeRow(1);
        } else {
//
            XWPFTableRow emptyRow = table.getRow(1);
            for (int i = 0; i < 5; i++) {
                table.addRow(emptyRow);
            }
        }

        return doc;

    }

    private XWPFDocument replaceText(String findText, String replaceText) {
        doc.getParagraphs().forEach(p -> p.getRuns().forEach(run -> {
            String text = run.text();
            if (text.contains(findText)) {
                run.setText(text.replace(findText, replaceText), 0);
            }
        }));

        return doc;
    }


}
