package pl.piotrziemianek;

import org.apache.xmlbeans.XmlException;
import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.Therapist;
import pl.piotrziemianek.service.DocCreator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, XmlException {
        DocCreator docCreator = new DocCreator();
        docCreator.create();
    }
}
