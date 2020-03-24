package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.piotrziemianek.util.TestUtil;
import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.TherapiesCard;
import pl.piotrziemianek.domain.Therapist;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TherapistDaoTest {

    private TherapistDao dao;
    private SessionFactory testSessionFactory;
    private TestUtil testUtil = new TestUtil();

    @BeforeEach
    void setUp() {
        dao = new TherapistDao();
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
        List<Therapist> all = dao.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void findById() {
        Optional<Therapist> foundTherapist = dao.findById(1);
        assertThat(foundTherapist).isPresent();
        Therapist therapist = foundTherapist.get();

    }

    @Test
    void create() {
        //no relations
        Therapist therapist = Therapist.builder()
                .academicDegree("dr")
                .firstName("Anna")
                .lastName("Bukowska")
                .build();

        int therapistId = dao.create(therapist);

        Session session = testSessionFactory.openSession();
        Therapist createdTherapist = session.get(Therapist.class, therapistId);
        session.close();

        assertThat(createdTherapist).isNotNull();

        Set<Patient> patients = createdTherapist.getPatients();

        assertThat(patients).isNotNull();
        assertThat(createdTherapist.getAcademicDegree()).isEqualTo(therapist.getAcademicDegree());
        assertThat(createdTherapist.getFirstName()).isEqualTo(therapist.getFirstName());
        assertThat(createdTherapist.getLastName()).isEqualTo(therapist.getLastName());

        //relations with new objects
        Patient patient = Patient.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .build();
        therapist = Therapist.builder()
                .academicDegree("mgr")
                .firstName("Leokadia")
                .lastName("Boo")
                .build();
        TherapiesCard therapiesCard = new TherapiesCard(LocalDate.now());

        therapist.addPatient(patient);
        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);

        therapistId = dao.create(therapist);

        session = testSessionFactory.openSession();
        createdTherapist = session.get(Therapist.class, therapistId);
        session.close();

        assertThat(createdTherapist).isNotNull();

        patients = createdTherapist.getPatients();
        List<TherapiesCard> therapiesCardList = createdTherapist.getTherapiesCardList();

        assertThat(patients).isNotNull();
        assertThat(patients).hasSize(1);
        assertThat(therapiesCardList).isNotNull();
        assertThat(therapiesCardList).hasSize(1);
        assertThat(createdTherapist.getAcademicDegree()).isEqualTo(therapist.getAcademicDegree());
        assertThat(createdTherapist.getFirstName()).isEqualTo(therapist.getFirstName());
        assertThat(createdTherapist.getLastName()).isEqualTo(therapist.getLastName());

        //relations with objects existing in db
        therapist = Therapist.builder()
                .academicDegree("prof")
                .firstName("Esmeralda")
                .lastName("Foo")
                .build();

        session = testSessionFactory.openSession();
        patient = session.get(Patient.class, 1);
        therapiesCard = session.get(TherapiesCard.class, 1);
        session.close();

        therapist.addPatient(patient);
        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);

        therapistId = dao.create(therapist);

        session = testSessionFactory.openSession();
        createdTherapist = session.get(Therapist.class, therapistId);
        session.close();

        assertThat(createdTherapist).isNotNull();

        patients = createdTherapist.getPatients();
        therapiesCardList = createdTherapist.getTherapiesCardList();

        assertThat(patients).isNotNull();
        assertThat(patients).hasSize(1);
        assertThat(therapiesCardList).isNotNull();
        assertThat(therapiesCardList).hasSize(1);
        assertThat(createdTherapist.getAcademicDegree()).isEqualTo(therapist.getAcademicDegree());
        assertThat(createdTherapist.getFirstName()).isEqualTo(therapist.getFirstName());
        assertThat(createdTherapist.getLastName()).isEqualTo(therapist.getLastName());
    }

    @Test
    void update() {
        Session session = testSessionFactory.openSession();
        Therapist therapist = session.get(Therapist.class, 1);
        Patient patient = session.get(Patient.class, 1);
        TherapiesCard therapiesCard = session.get(TherapiesCard.class, 1);
        session.close();

        therapist.setAcademicDegree("new degree");
        therapist.setFirstName("new name");
        therapist.setLastName("new lastname");
        therapist.addPatient(patient);
        therapiesCard.setTherapist(therapist);

        int updateId = dao.update(therapist);

        session = testSessionFactory.openSession();
        Therapist updatedTherapist = session.get(Therapist.class, updateId);
        session.close();

        assertThat(updatedTherapist).isNotNull();
        assertThat(updatedTherapist.getAcademicDegree()).isEqualTo("new degree");
        assertThat(updatedTherapist.getFirstName()).isEqualTo("new name");
        assertThat(updatedTherapist.getLastName()).isEqualTo("new lastname");
        assertThat(updatedTherapist.getPatients()).hasSize(1);
        assertThat(updatedTherapist.getTherapiesCardList()).hasSize(1);
    }

    @Test
    void createOrUpdate() {
        Therapist therapist = Therapist.builder()
                .academicDegree("dr")
                .firstName("Anna")
                .lastName("Bukowska")
                .build();

        int therapistId = dao.createOrUpdate(therapist);

        Session session = testSessionFactory.openSession();
        Therapist createdTherapist = session.get(Therapist.class, therapistId);
        session.close();

        assertThat(createdTherapist).isNotNull();

        Set<Patient> patients = createdTherapist.getPatients();

        assertThat(patients).isNotNull();
        assertThat(createdTherapist.getAcademicDegree()).isEqualTo(therapist.getAcademicDegree());
        assertThat(createdTherapist.getFirstName()).isEqualTo(therapist.getFirstName());
        assertThat(createdTherapist.getLastName()).isEqualTo(therapist.getLastName());

        //update
        session = testSessionFactory.openSession();
        therapist = session.get(Therapist.class, 1);
        Patient patient = session.get(Patient.class, 1);
        TherapiesCard therapiesCard = session.get(TherapiesCard.class, 1);
        session.close();

        therapist.setAcademicDegree("new degree");
        therapist.setFirstName("new name");
        therapist.setLastName("new lastname");
        therapist.addPatient(patient);
        therapiesCard.setTherapist(therapist);

        int updateId = dao.createOrUpdate(therapist);

        session = testSessionFactory.openSession();
        Therapist updatedTherapist = session.get(Therapist.class, updateId);
        session.close();

        assertThat(updatedTherapist.getAcademicDegree()).isEqualTo("new degree");
        assertThat(updatedTherapist.getFirstName()).isEqualTo("new name");
        assertThat(updatedTherapist.getLastName()).isEqualTo("new lastname");
        assertThat(updatedTherapist.getPatients()).hasSize(1);
        assertThat(updatedTherapist.getTherapiesCardList()).hasSize(1);
    }

    @Test
    void delete() {
        //get therapist who has Patient and TherapyCard
        Session session = testSessionFactory.openSession();
        Therapist existingTherapist = session.get(Therapist.class, 2);
        session.close();


        int existingTherapistId = existingTherapist.getId();

        boolean delete = dao.delete(existingTherapistId);

        session = testSessionFactory.openSession();
        Therapist deletedTherapist = session.get(Therapist.class, existingTherapistId);
        session.close();

        assertThat(delete).isTrue();
        assertThat(deletedTherapist).isNull();

        //try to remove a non-existent therapist
        delete = dao.delete(9);
        assertThat(delete).isFalse();
    }

    @Test
    void deleteAll() {
        boolean deleteAll = dao.deleteAll();

        Session session = testSessionFactory.openSession();
        List<Therapist> therapists = session.createQuery("from Therapist", Therapist.class).list();
        session.close();

        assertThat(deleteAll).isTrue();
        assertThat(therapists).isEmpty();
    }
}