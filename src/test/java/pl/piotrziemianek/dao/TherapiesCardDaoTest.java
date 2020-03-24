package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.TherapiesCard;
import pl.piotrziemianek.domain.Therapist;
import pl.piotrziemianek.domain.Therapy;
import pl.piotrziemianek.util.TestUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TherapiesCardDaoTest {

    private TherapiesCardDao dao;
    private SessionFactory testSessionFactory;
    private TestUtil testUtil = new TestUtil();


    @BeforeEach
    void setUp() {
        dao = new TherapiesCardDao();
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
        List<TherapiesCard> all = dao.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void findById() {
        Optional<TherapiesCard> foundTherapiesCard = dao.findById(2);
        assertThat(foundTherapiesCard).isPresent();
        TherapiesCard therapiesCard = foundTherapiesCard.get();
        assertThat(therapiesCard).isNotNull();
        assertThat(therapiesCard.getYearMonth()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(therapiesCard
                .getPatient()
                .getLastName())
                .isEqualTo("Polak");
        assertThat(therapiesCard
                .getTherapist()
                .getLastName())
                .isEqualTo("Maj");
        assertThat(therapiesCard.getTherapies()).hasSize(1);
    }

    @Test
    void create() {
        Session session = testSessionFactory.openSession();
        Patient patient = session.get(Patient.class, 1);
        Therapist therapist = session.get(Therapist.class, 1);
        Therapy therapy = session.get(Therapy.class, 1);
        session.close();

        TherapiesCard therapiesCard = new TherapiesCard(LocalDate.of(2020, 2, 1));

        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);
        therapiesCard.addTherapy(therapy);

        int therapiesCardId = dao.create(therapiesCard);

        session = testSessionFactory.openSession();
        TherapiesCard createdTherapiesCard = session.get(TherapiesCard.class, therapiesCardId);
        session.close();

        assertThat(createdTherapiesCard).isNotNull();

        assertThat(createdTherapiesCard.getYearMonth()).isEqualTo(LocalDate.of(2020, 2, 1));
        assertThat(createdTherapiesCard.getTherapies()).hasSize(1);
        assertThat(createdTherapiesCard.getTherapist()).isEqualTo(therapist);
        assertThat(createdTherapiesCard.getPatient()).isEqualTo(patient);
    }

    @Test
    void update() {
        Session session = testSessionFactory.openSession();
        TherapiesCard therapiesCard = session.get(TherapiesCard.class, 1);
        Patient patient = session.get(Patient.class, 1);
        Therapist therapist = session.get(Therapist.class, 1);
        Therapy therapy = session.get(Therapy.class, 1);
        session.close();

        therapiesCard.setYearMonth(LocalDate.of(2020, 2, 1));
        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);
        therapiesCard.addTherapy(therapy);

        int updateId = dao.update(therapiesCard);

        session = testSessionFactory.openSession();
        TherapiesCard updatedTherapiesCard = session.get(TherapiesCard.class, updateId);
        session.close();

        assertThat(updatedTherapiesCard).isNotNull();
        assertThat(updatedTherapiesCard.getYearMonth()).isEqualTo(LocalDate.of(2020, 2, 1));
        assertThat(updatedTherapiesCard.getPatient()).isEqualTo(patient);
        assertThat(updatedTherapiesCard.getTherapist()).isEqualTo(therapist);
        assertThat(updatedTherapiesCard.getTherapies()).hasSize(1);
    }

    @Test
    void createOrUpdate() {
        Session session = testSessionFactory.openSession();
        Patient patient = session.get(Patient.class, 1);
        Therapist therapist = session.get(Therapist.class, 1);
        Therapy therapy = session.get(Therapy.class, 1);
        session.close();

        TherapiesCard therapiesCard = new TherapiesCard(LocalDate.of(2020, 2, 1));

        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);
        therapiesCard.addTherapy(therapy);

        int therapiesCardId = dao.createOrUpdate(therapiesCard);

        session = testSessionFactory.openSession();
        TherapiesCard createdTherapiesCard = session.get(TherapiesCard.class, therapiesCardId);
        session.close();

        assertThat(createdTherapiesCard).isNotNull();

        assertThat(createdTherapiesCard.getYearMonth()).isEqualTo(LocalDate.of(2020, 2, 1));
        assertThat(createdTherapiesCard.getTherapies()).hasSize(1);
        assertThat(createdTherapiesCard.getTherapist()).isEqualTo(therapist);
        assertThat(createdTherapiesCard.getPatient()).isEqualTo(patient);

        //update
        session = testSessionFactory.openSession();
        therapiesCard = session.get(TherapiesCard.class, 1);
        patient = session.get(Patient.class, 1);
        therapist = session.get(Therapist.class, 1);
        therapy = session.get(Therapy.class, 1);
        session.close();

        therapiesCard.setYearMonth(LocalDate.of(2020, 3, 1));
        therapiesCard.setTherapist(therapist);
        therapiesCard.setPatient(patient);
        therapiesCard.addTherapy(therapy);

        int updateId = dao.createOrUpdate(therapiesCard);

        session = testSessionFactory.openSession();
        TherapiesCard updatedTherapiesCard = session.get(TherapiesCard.class, updateId);
        session.close();

        assertThat(updatedTherapiesCard).isNotNull();
        assertThat(updatedTherapiesCard.getYearMonth()).isEqualTo(LocalDate.of(2020, 3, 1));
        assertThat(updatedTherapiesCard.getPatient()).isEqualTo(patient);
        assertThat(updatedTherapiesCard.getTherapist()).isEqualTo(therapist);
        assertThat(updatedTherapiesCard.getTherapies()).hasSize(1);

    }

    @Test
    void delete() {
        Session session = testSessionFactory.openSession();
        //get therapyCard what has Therapy, Therapist and Patient
        TherapiesCard existingTherapiesCard = session.get(TherapiesCard.class, 2);
        session.close();

        int existingTherapiesCardId = existingTherapiesCard.getId();

        boolean delete = dao.delete(existingTherapiesCardId);

        session = testSessionFactory.openSession();
        TherapiesCard deletedTherapiesCard = session.get(TherapiesCard.class, existingTherapiesCardId);
        session.close();

        assertThat(delete).isTrue();
        assertThat(deletedTherapiesCard).isNull();

        //try to remove a non-existent therapies card
        delete = dao.delete(9);
        assertThat(delete).isFalse();

    }

    @Test
    void deleteAll() {
        boolean deleteAll = dao.deleteAll();

        Session session = testSessionFactory.openSession();
        List<TherapiesCard> therapiesCardList = session.createQuery("from TherapiesCard", TherapiesCard.class).list();
        session.close();

        assertThat(deleteAll).isTrue();
        assertThat(therapiesCardList).isEmpty();

    }
}