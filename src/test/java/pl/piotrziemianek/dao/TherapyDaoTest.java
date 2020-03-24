package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.piotrziemianek.domain.*;
import pl.piotrziemianek.util.TestUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TherapyDaoTest {

    private TherapyDao dao;
    private SessionFactory testSessionFactory;
    private TestUtil testUtil = new TestUtil();


    @BeforeEach
    void setUp() {
        dao = new TherapyDao();
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
        List<Therapy> all = dao.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void findById() {
        Optional<Therapy> foundTherapy = dao.findById(1);
        assertThat(foundTherapy).isPresent();
        Therapy therapy = foundTherapy.get();
        assertThat(therapy.getTherapyDate()).isEqualTo(LocalDate.of(2020, 1, 5));
        assertThat(therapy.getSubjects()).hasSize(1);
        assertThat(therapy.getSupports()).hasSize(1);
    }

    @Test
    void create() {
        Therapy therapy = new Therapy(LocalDate.of(2018, 12, 1));
        Session session = testSessionFactory.openSession();
        TherapiesCard therapiesCard = new TherapiesCard();
        Support support = session.get(Support.class, 1);
        Subject subject = session.get(Subject.class, 1);
        session.close();

        therapy.addSupport(support);
        therapy.addSubject(subject);
        therapiesCard.addTherapy(therapy);

        int therapyId = dao.create(therapy);

        session = testSessionFactory.openSession();
        Therapy createdTherapy = session.get(Therapy.class, therapyId);
        session.close();

        assertThat(createdTherapy).isNotNull();
        assertThat(createdTherapy.getTherapyDate()).isEqualTo(LocalDate.of(2018, 12, 1));
        assertThat(createdTherapy.getSupports()).hasSize(1);
        assertThat(createdTherapy.getSubjects()).hasSize(1);
        assertThat(createdTherapy.getTherapiesCard()).isEqualTo(therapiesCard);
        assertThat(createdTherapy.getTherapiesCard().getTherapies()).contains(createdTherapy);
    }

    @Test
    void update() {
        Session session = testSessionFactory.openSession();
        Therapy therapy = session.get(Therapy.class, 2);
        session.close();

        therapy.setTherapyDate(LocalDate.of(2020, 1, 13));
        therapy.addSubject(new Subject("new subject"));
        therapy.addSupport(new Support("new support"));

        int therapiesCardId = therapy.getTherapiesCard().getId();
        therapy.getTherapiesCard().removeTherapy(therapy);

        int updateId = dao.update(therapy);

        session = testSessionFactory.openSession();
        Therapy updatedTherapy = session.get(Therapy.class, updateId);
        TherapiesCard therapiesCard = session.get(TherapiesCard.class, therapiesCardId);
        session.close();

        assertThat(updatedTherapy).isNotNull();
        assertThat(updatedTherapy.getTherapyDate()).isEqualTo(LocalDate.of(2020, 1, 13));
        assertThat(updatedTherapy.getSubjects()).hasSize(2);
        assertThat(updatedTherapy.getSupports()).hasSize(2);
        assertThat(updatedTherapy.getTherapiesCard()).isNull();
        assertThat(therapiesCard.getTherapies()).isEmpty();


    }

    @Test
    void createOrUpdate() {
        Therapy therapy = new Therapy(LocalDate.of(2019, 12, 1));
        Session session = testSessionFactory.openSession();
        TherapiesCard therapiesCard = session.get(TherapiesCard.class, 1);
        Support support = session.get(Support.class, 1);
        Subject subject = session.get(Subject.class, 1);
        session.close();

        therapy.addSupport(support);
        therapy.addSubject(subject);
        therapiesCard.addTherapy(therapy);

        int therapyId = dao.createOrUpdate(therapy);

        session = testSessionFactory.openSession();
        Therapy createdTherapy = session.get(Therapy.class, therapyId);
        session.close();

        assertThat(createdTherapy).isNotNull();
        assertThat(createdTherapy.getTherapyDate()).isEqualTo(LocalDate.of(2019, 12, 1));
        assertThat(createdTherapy.getSupports()).hasSize(1);
        assertThat(createdTherapy.getSubjects()).hasSize(1);
        assertThat(createdTherapy.getTherapiesCard()).isEqualTo(therapiesCard);

        //update

        session = testSessionFactory.openSession();
        therapy = session.get(Therapy.class, 2);
        session.close();

        therapy.setTherapyDate(LocalDate.of(2020, 1, 13));
        therapy.addSubject(new Subject("new subject"));
        therapy.addSupport(new Support("new support"));
        therapy.getTherapiesCard().removeTherapy(therapy);

        int updateId = dao.createOrUpdate(therapy);

        session = testSessionFactory.openSession();
        Therapy updatedTherapy = session.get(Therapy.class, updateId);
        session.close();

        assertThat(updatedTherapy).isNotNull();
        assertThat(updatedTherapy.getTherapyDate()).isEqualTo(LocalDate.of(2020, 1, 13));
        assertThat(updatedTherapy.getSubjects()).hasSize(2);
        assertThat(updatedTherapy.getSupports()).hasSize(2);
        assertThat(updatedTherapy.getTherapiesCard()).isNull();
    }

    @Test
    void delete() {
        Session session = testSessionFactory.openSession();
        //get therapy what has Subject, Support and TherapyCard
        Therapy existingTherapy = session.get(Therapy.class, 2);

        session.close();

        int existingTherapyId = existingTherapy.getId();

        boolean delete = dao.delete(existingTherapyId);

        session = testSessionFactory.openSession();
        Therapy deletedTherapy = session.get(Therapy.class, existingTherapyId);
        session.close();

        assertThat(delete).isTrue();
        assertThat(deletedTherapy).isNull();

        //try to remove a non-existent therapy
        delete = dao.delete(9);
        assertThat(delete).isFalse();
    }

    @Test
    void deleteAll() {
        boolean deleteAll = dao.deleteAll();

        Session session = testSessionFactory.openSession();
        List<Therapy> therapies = session.createQuery("from Therapy", Therapy.class).list();
        session.close();

        assertThat(deleteAll).isTrue();
        assertThat(therapies).isEmpty();
    }
}