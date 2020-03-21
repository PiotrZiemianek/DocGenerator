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
import java.util.*;

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
        testUtil.fillDB(testSessionFactory);
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
        //no relations
        Patient patient = Patient.builder()
                .firstName("Piotr")
                .lastName("Ziemianek")
                .build();

        int patientId = dao.create(patient);

        Session session = testSessionFactory.openSession();
        Patient createdPatient = session.get(Patient.class, patientId);
        session.close();

        assertThat(createdPatient).isNotNull();

        Set<Therapist> therapists = createdPatient.getTherapists();

        assertThat(therapists).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo(createdPatient.getFirstName());
        assertThat(patient.getLastName()).isEqualTo(createdPatient.getLastName());

        //relations with new objects
        patient = Patient.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .build();
        Therapist therapist = Therapist.builder()
                .academicDegree("mgr")
                .firstName("Leokadia")
                .lastName("Boo")
                .build();
        TherapiesCard therapiesCard = new TherapiesCard(LocalDate.now());

        patient.addTherapist(therapist);
        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);

        patientId = dao.create(patient);

        session = testSessionFactory.openSession();
        createdPatient = session.get(Patient.class, patientId);
        session.close();

        assertThat(createdPatient).isNotNull();

        therapists = createdPatient.getTherapists();
        List<TherapiesCard> therapiesCardList = createdPatient.getTherapiesCardList();

        assertThat(therapists).isNotNull();
        assertThat(therapists).hasSize(1);
        assertThat(therapiesCardList).isNotNull();
        assertThat(therapiesCardList).hasSize(1);
        assertThat(patient.getFirstName()).isEqualTo(createdPatient.getFirstName());
        assertThat(patient.getLastName()).isEqualTo(createdPatient.getLastName());

        //relations with objects existing in db
        patient = Patient.builder()
                .firstName("Alan")
                .lastName("Malik")
                .build();
        session = testSessionFactory.openSession();
        therapist = session.get(Therapist.class, 1);
        therapiesCard = session.get(TherapiesCard.class, 1);
        session.close();

        patient.addTherapist(therapist);
        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);

        patientId = dao.create(patient);

        session = testSessionFactory.openSession();
        createdPatient = session.get(Patient.class, patientId);
        therapists = createdPatient.getTherapists();
        therapiesCardList = createdPatient.getTherapiesCardList();
        session.close();

        assertThat(therapists).isNotNull();
        assertThat(therapists).hasSize(1);
        assertThat(therapiesCardList).isNotNull();
        assertThat(therapiesCardList).hasSize(1);
        assertThat(patient.getFirstName()).isEqualTo(createdPatient.getFirstName());
        assertThat(patient.getLastName()).isEqualTo(createdPatient.getLastName());


    }

    @Test
    void update() {

        Session session = testSessionFactory.openSession();
        Patient patient = session.get(Patient.class, 1);
        Therapist therapist = session.get(Therapist.class, 1);
        TherapiesCard therapiesCard = session.get(TherapiesCard.class, 1);
        session.close();

        patient.setFirstName("new name");
        patient.setLastName("new lastname");
        patient.addTherapist(therapist);
        therapiesCard.setPatient(patient);

        int updateId = dao.update(patient);

        session = testSessionFactory.openSession();
        Patient updatedPatient = session.get(Patient.class, updateId);
        session.close();

        assertThat(updatedPatient).isNotNull();
        assertThat(updatedPatient.getFirstName()).isEqualTo("new name");
        assertThat(updatedPatient.getLastName()).isEqualTo("new lastname");
        assertThat(updatedPatient.getTherapists()).hasSize(1);
        assertThat(updatedPatient.getTherapiesCardList()).hasSize(1);
    }

    @Test
    void createOrUpdate() {
        Patient patient = Patient.builder()
                .firstName("Piotr")
                .lastName("Ziemianek")
                .build();

        int patientId = dao.createOrUpdate(patient);

        Session session = testSessionFactory.openSession();
        Patient createdPatient = session.get(Patient.class, patientId);
        Set<Therapist> therapists = createdPatient.getTherapists();
        session.close();

        assertThat(therapists).isNotNull();
        assertThat(patient.getFirstName()).isEqualTo(createdPatient.getFirstName());
        assertThat(patient.getLastName()).isEqualTo(createdPatient.getLastName());

        //update
        session = testSessionFactory.openSession();
        patient = session.get(Patient.class, 1);
        Therapist therapist = session.get(Therapist.class, 1);
        TherapiesCard therapiesCard = session.get(TherapiesCard.class, 1);
        session.close();

        patient.setFirstName("new name");
        patient.setLastName("new lastname");
        patient.addTherapist(therapist);
        therapiesCard.setPatient(patient);

        int updateId = dao.createOrUpdate(patient);

        session = testSessionFactory.openSession();
        Patient updatedPatient = session.get(Patient.class, updateId);
        session.close();

        assertThat(updatedPatient.getFirstName()).isEqualTo("new name");
        assertThat(updatedPatient.getLastName()).isEqualTo("new lastname");
        assertThat(updatedPatient.getTherapists()).hasSize(1);
        assertThat(updatedPatient.getTherapiesCardList()).hasSize(1);
    }

    @Test
    void delete() {
        Session session = testSessionFactory.openSession();
        //get patient who has Therapist and TherapyCard
        Patient existingPatient = session.get(Patient.class, 5);

        session.close();


        int existingPatientId = existingPatient.getId();

        boolean delete = dao.delete(existingPatientId);

        session = testSessionFactory.openSession();
        Patient deletedPatient = session.get(Patient.class, existingPatientId);

        session.close();

        assertThat(delete).isTrue();
        assertThat(deletedPatient).isNull();
    }

    @Test
    void deleteAll() {
        boolean deleteAll = dao.deleteAll();

        Session session = testSessionFactory.openSession();
        List<Patient> patients = session.createQuery("from Patient", Patient.class).list();
        session.close();

        assertThat(deleteAll).isTrue();
        assertThat(patients).isEmpty();

    }

}