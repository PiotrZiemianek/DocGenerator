package pl.piotrziemianek;

import org.apache.xmlbeans.XmlException;
import pl.piotrziemianek.domain.*;
import pl.piotrziemianek.service.DocCreator;

import java.io.IOException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws IOException {
        TherapiesCard therapiesCard = new TherapiesCard(LocalDate.now());

        Patient patient = Patient.builder()
                .firstName("Michał")
                .lastName("Polak")
                .build();

        Therapist therapist = Therapist.builder()
                .academicDegree("mgr")
                .firstName("Katarzyna")
                .lastName("Dunag")
                .build();

        Therapy therapy1 = new Therapy(LocalDate.of(2020,1,5));
        therapy1.addSubject(new Subject("Dobry temat1"));
        therapy1.addSubject(new Subject("Najlepszy temat"));
        therapy1.addSupport(new Support("Dobre wspomaganie1"));

        Therapy therapy2 = new Therapy(LocalDate.now());
        therapy2.addSubject(new Subject("Dobry temat2"));
        therapy2.addSupport(new Support("Dobre wspomaganie2"));

        therapiesCard.addTherapy(therapy1);
        therapiesCard.addTherapy(therapy2);
        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);

        DocCreator docCreator = new DocCreator(therapiesCard);
        docCreator.create();
    }
}
