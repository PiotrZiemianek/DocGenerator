package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.piotrziemianek.domain.Subject;
import pl.piotrziemianek.domain.Therapy;
import pl.piotrziemianek.util.TestUtil;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SubjectDaoTest {
    private SubjectDao dao;
    private SessionFactory testSessionFactory;
    private TestUtil testUtil = new TestUtil();


    @BeforeEach
    void setUp() {
        dao = new SubjectDao();
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
        List<Subject> all = dao.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void findById() {
        Optional<Subject> foundPatient = dao.findById(1);
        assertThat(foundPatient).isPresent();
        Subject subject = foundPatient.get();
        assertThat(subject.getSubject()).isEqualTo("Dobry temat1");
    }

    @Test
    void create() {
        Subject subject = new Subject("New subject");
        int subjectId = dao.create(subject);

        Session session = testSessionFactory.openSession();
        Subject createdSubject = session.get(Subject.class, subjectId);
        session.close();

        assertThat(createdSubject).isNotNull();
        assertThat(createdSubject.getSubject()).isEqualTo("New subject");
    }

    @Test
    void update() {
        Session session = testSessionFactory.openSession();
        Subject subject = session.get(Subject.class, 1);
        session.close();

        subject.setSubject("Updated subject");

        int updateId = dao.update(subject);

        session = testSessionFactory.openSession();
        Subject updatedSubject = session.get(Subject.class, updateId);
        session.close();

        assertThat(updatedSubject).isNotNull();
        assertThat(updatedSubject.getSubject()).isEqualTo("Updated subject");
    }

    @Test
    void createOrUpdate() {
        Subject subject = new Subject("New subject");
        int subjectId = dao.createOrUpdate(subject);

        Session session = testSessionFactory.openSession();
        Subject createdSubject = session.get(Subject.class, subjectId);
        session.close();

        assertThat(createdSubject).isNotNull();
        assertThat(createdSubject.getSubject()).isEqualTo("New subject");

        //update
        session = testSessionFactory.openSession();
        subject = session.get(Subject.class, 1);
        session.close();

        subject.setSubject("Updated subject");

        int updateId = dao.createOrUpdate(subject);

        session = testSessionFactory.openSession();
        Subject updatedSubject = session.get(Subject.class, updateId);
        session.close();

        assertThat(updatedSubject).isNotNull();
        assertThat(updatedSubject.getSubject()).isEqualTo("Updated subject");
    }

    @Test
    void delete() {
        Session session = testSessionFactory.openSession();
        Subject existingSubject = session.get(Subject.class, 3);
        session.close();

        int existingSubjectId = existingSubject.getId();

        boolean delete = dao.delete(existingSubjectId);

        session = testSessionFactory.openSession();
        Subject deletedSubject = session.get(Subject.class, existingSubjectId);
        session.close();

        assertThat(delete).isTrue();
        assertThat(deletedSubject).isNull();
    }

}