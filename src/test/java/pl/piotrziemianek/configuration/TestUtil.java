package pl.piotrziemianek.configuration;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import pl.piotrziemianek.domain.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class TestUtil {

    public SessionFactory getTestSessionFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernateTest.cfg.xml")
                .build();
        Metadata metadata = new MetadataSources(registry)
                .buildMetadata();

        return metadata.buildSessionFactory();
    }

    public void fillDB(SessionFactory sessionFactory) {
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

        Therapy therapy1 = new Therapy(LocalDate.now());
        therapy1.addSubject(new Subject("Dobry temat1"));
        therapy1.addSupport(new Support("Dobre wspomaganie1"));

        Therapy therapy2 = new Therapy(LocalDate.now());
        therapy2.addSubject(new Subject("Dobry temat2"));
        therapy2.addSupport(new Support("Dobre wspomaganie2"));

        TherapiesCard therapiesCard1 = new TherapiesCard(LocalDate.now());
        TherapiesCard therapiesCard2 = new TherapiesCard(LocalDate.now());


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

