package pl.piotrziemianek;

import org.apache.xmlbeans.XmlException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.piotrziemianek.dao.TherapiesCardDao;
import pl.piotrziemianek.domain.*;
import pl.piotrziemianek.service.DocCreator;
import pl.piotrziemianek.util.HibernateUtil;

import java.io.IOException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws IOException {
      fillDB(HibernateUtil.getSessionFactory());
    }
    public static void fillDB(SessionFactory sessionFactory) {
        Patient patient1 = Patient.builder()
                .firstName("Andrzej")
                .lastName("Nowak")
                .build();
        Patient patient2 = Patient.builder()
                .firstName("Adam")
                .lastName("Kowalski")
                .build();
        Patient patient3 = Patient.builder()
                .firstName("Joanna")
                .lastName("Szewczyk")
                .build();
        Patient patient4 = Patient.builder()
                .firstName("Małgorzata")
                .lastName("Masny")
                .build();
        Patient patient5 = Patient.builder()
                .firstName("Michał")
                .lastName("Polak")
                .build();

        Therapist therapist1 = Therapist.builder()
                .academicDegree("mgr")
                .firstName("Katarzyna")
                .lastName("Dunag")
                .build();
        Therapist therapist2 = Therapist.builder()
                .academicDegree("dr")
                .firstName("Ela")
                .lastName("Maj")
                .build();

        Therapy therapy1 = new Therapy(LocalDate.of(2020,1,5));
        therapy1.addSubject(new Subject("Dobry temat1"));
        therapy1.addSupport(new Support("Dobre wspomaganie1"));

        Therapy therapy2 = new Therapy(LocalDate.now());
        therapy2.addSubject(new Subject("Dobry temat2"));
        therapy2.addSupport(new Support("Dobre wspomaganie2"));

        Subject subject3 = new Subject();
        Support support3 = new Support();

        TherapiesCard therapiesCard1 = new TherapiesCard(LocalDate.now());
        TherapiesCard therapiesCard2 = new TherapiesCard(LocalDate.of(2020,1,1));


        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(therapy1);
            session.persist(therapy2);
            session.persist(therapist1);
            session.persist(therapist2);
            session.persist(therapiesCard1);
            session.persist(therapiesCard2);
            session.persist(patient1);
            session.persist(patient2);
            session.persist(patient3);
            session.persist(patient4);
            session.persist(patient5);
            session.persist(subject3);
            session.persist(support3);

            patient5.addTherapist(therapist2);
            therapiesCard2.addTherapy(therapy2);
            therapiesCard2.setPatient(patient5);
            therapiesCard2.setTherapist(therapist2);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }

        session.close();
    }
}
