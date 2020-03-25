package pl.piotrziemianek.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sun.istack.NotNull;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import pl.piotrziemianek.domain.TherapiesCard;

public class DocCreator {
    XWPFDocument doc = new XWPFDocument(new FileInputStream("template.docx"));
    TherapiesCard therapiesCard;

    public DocCreator() throws IOException {
        //todo temporary for test
    }

    public DocCreator(@NotNull TherapiesCard therapiesCard) throws IOException {
        this.therapiesCard = therapiesCard;
    }

    public void create() throws IOException, XmlException {
//        doc = replaceText("$childName","Cokolwiek");

        XWPFTable table = doc.getTableArray(0);
        XWPFTableRow oldRow = table.getRow(1);
        CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
        XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

//        int i = 1;
//        for (XWPFTableCell cell : newRow.getTableCells()) {
//            for (XWPFParagraph paragraph : cell.getParagraphs()) {
//                for (XWPFRun run : paragraph.getRuns()) {
//                    run.setText("New row 3 cell " + i++, 0);
//                }
//            }
//        }

        table.addRow(newRow);
        doc.write(new FileOutputStream("result.docx"));
        doc.close();
    }
//    private XWPFDocument replaceText(String findText, String replaceText){
//        doc.getParagraphs().forEach(p ->{
//            p.getRuns().forEach(run -> {
//                String text = run.text();
//                if(text.contains(findText)) {
//                    run.setText(text.replace(findText, replaceText), 0);
//                }
//            });
//        });
//
//        return doc;
//    }


}
