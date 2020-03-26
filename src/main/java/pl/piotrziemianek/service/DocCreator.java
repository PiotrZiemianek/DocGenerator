package pl.piotrziemianek.service;

import com.sun.istack.NotNull;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import pl.piotrziemianek.domain.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class DocCreator {
    private XWPFDocument doc = new XWPFDocument(new FileInputStream("template.docx"));
    private TherapiesCard therapiesCard;


    public DocCreator(@NotNull TherapiesCard therapiesCard) throws IOException {
        this.therapiesCard = therapiesCard;
    }

    public void create() throws IOException {
        Patient patient = therapiesCard.getPatient();
        Therapist therapist = therapiesCard.getTherapist();
        String yearMonth = therapiesCard.getYearMonthString();
        List<Therapy> therapies = therapiesCard.getTherapies();

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
                } catch (XmlException e) {
                    e.printStackTrace();
                }
                XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

                //TherapyDate
                newRow.getCell(0)
                        .getParagraphArray(0).createRun()
                        .setText(therapy.getTherapyDate().toString());

                //Subjects
                XWPFTableCell subjectsCell = newRow.getCell(1);
                for (Subject subject : therapy.getSubjects()) {
                    String sub = subject.getSubject();
                    subjectsCell.addParagraph().createRun().setText(sub);
                }
                subjectsCell.removeParagraph(0);

                //Support
                XWPFTableCell supportCell = newRow.getCell(2);
                for (Support support : therapy.getSupports()) {
                    String supp = support.getSupport();
                    supportCell.addParagraph().createRun().setText(supp);
                }
                supportCell.removeParagraph(0);

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

        if (patient != null && yearMonth != null) {
            doc.write(new FileOutputStream(patient.getLastName() + yearMonth + ".docx"));
        } else {
            doc.write(new FileOutputStream("karta_terapii" + LocalDate.now() + ".docx"));
        }
        doc.close();
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
