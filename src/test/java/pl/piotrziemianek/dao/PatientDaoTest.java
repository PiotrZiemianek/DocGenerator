package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.piotrziemianek.configuration.TestUtil;
import pl.piotrziemianek.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PatientDaoTest {
    private PatientDao dao;
    SessionFactory testSessionFactory;
    private TestUtil testUtil = new TestUtil();


    @BeforeEach
    void setUp() {
        dao = new PatientDao();
        testSessionFactory = testUtil.getTestSessionFactory();
        dao.setSessionFactoryOnlyForTests(testSessionFactory);
        fillDB(testSessionFactory);
    }

    @AfterEach
    void tearDown() {
        testSessionFactory.close();
    }

    @Test
    void findAll() {
        List<Patient> all = dao.findAll();
        assertThat(all).hasSize(5);
    }

    @Test
    void findById() {
        Optional<Patient> foundPatient = dao.findById(1);
        assertThat(foundPatient).isPresent();
        Patient patient = foundPatient.get();
        assertThat(patient.getFirstName()).isEqualTo("Andrzej");
        assertThat(patient.getLastName()).isEqualTo("Nowak");
    }

    @Test
    void create() {
        Patient patient = Patient.builder()
                .firstName("Piotr")
                .lastName("Ziemianek")
                .build();

        int patientId = dao.create(patient);

        Session session = testSessionFactory.openSession();
        Patient createdPatient = session.get(Patient.class, patientId);
        Set<Therapist> therapists = createdPatient.getTherapists();
        session.close();

        assertThat(therapists).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo(createdPatient.getFirstName());
        assertThat(patient.getLastName()).isEqualTo(createdPatient.getLastName());
    }

    @Test
    void update() {

        Session session = testSessionFactory.openSession();
        Patient patient = session.get(Patient.class, 1);
        patient.getTherapists();
        patient.getTherapiesCardList();

        session.close();

        patient.setFirstName("new name");
        patient.setLastName("new lastname");

        int updateId = dao.update(patient);

        session = testSessionFactory.openSession();
        Patient updatesPatient = session.get(Patient.class, updateId);
        session.close();

        assertThat(updatesPatient.getFirstName()).isEqualTo("new name");
        assertThat(updatesPatient.getLastName()).isEqualTo("new lastname");
    }

    @Test
    void createOrUpdate() {
    }

    @Test
    void delete() {
    }

    @Test
    void deleteAll() {
    }

    private void fillDB(SessionFactory sessionFactory) {
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

        Therapist therapist = Therapist.builder()
                .academicDegree("mgr")
                .firstName("Katarzyna")
                .lastName("Ziemianek")
                .build();

        Therapy therapy = new Therapy(LocalDate.now());
        therapy.addSubject(new Subject("Dobry temat"));
        therapy.addSupport(new Support("Dobre wspomaganie"));

        TherapiesCard therapiesCard = new TherapiesCard(LocalDate.now());

        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.save(patient1);
            session.save(patient2);
            session.save(patient3);
            session.save(patient4);
            session.save(patient5);

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }

        session.close();
    }
}