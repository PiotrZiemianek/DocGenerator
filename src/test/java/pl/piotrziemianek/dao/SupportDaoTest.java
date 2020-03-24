package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.piotrziemianek.domain.Support;
import pl.piotrziemianek.util.TestUtil;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SupportDaoTest {
    private SupportDao dao;
    private SessionFactory testSessionFactory;
    private TestUtil testUtil = new TestUtil();


    @BeforeEach
    void setUp() {
        dao = new SupportDao();
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
        List<Support> all = dao.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void findById() {
        Optional<Support> foundPatient = dao.findById(1);
        assertThat(foundPatient).isPresent();
        Support support = foundPatient.get();
        assertThat(support.getSupport()).isEqualTo("Dobre wspomaganie1");
    }

    @Test
    void create() {
        Support support = new Support("New support");
        int supportId = dao.create(support);

        Session session = testSessionFactory.openSession();
        Support createdSupport = session.get(Support.class, supportId);
        session.close();

        assertThat(createdSupport).isNotNull();
        assertThat(createdSupport.getSupport()).isEqualTo("New support");
    }

    @Test
    void update() {
        Session session = testSessionFactory.openSession();
        Support support = session.get(Support.class, 1);
        session.close();

        support.setSupport("Updated support");

        int updateId = dao.update(support);

        session = testSessionFactory.openSession();
        Support updatedSupport = session.get(Support.class, updateId);
        session.close();

        assertThat(updatedSupport).isNotNull();
        assertThat(updatedSupport.getSupport()).isEqualTo("Updated support");
    }

    @Test
    void createOrUpdate() {
        Support support = new Support("New support");
        int supportId = dao.create(support);

        Session session = testSessionFactory.openSession();
        Support createdSupport = session.get(Support.class, supportId);
        session.close();

        assertThat(createdSupport).isNotNull();
        assertThat(createdSupport.getSupport()).isEqualTo("New support");

        //update
        session = testSessionFactory.openSession();
        support = session.get(Support.class, 1);
        session.close();

        support.setSupport("Updated support");

        int updateId = dao.update(support);

        session = testSessionFactory.openSession();
        Support updatedSupport = session.get(Support.class, updateId);
        session.close();

        assertThat(updatedSupport).isNotNull();
        assertThat(updatedSupport.getSupport()).isEqualTo("Updated support");
    }

    @Test
    void delete() {
        Session session = testSessionFactory.openSession();
        Support existingSupport = session.get(Support.class, 3);
        session.close();

        int existingSupportId = existingSupport.getId();

        boolean delete = dao.delete(existingSupportId);

        session = testSessionFactory.openSession();
        Support deletedSupport = session.get(Support.class, existingSupportId);
        session.close();

        assertThat(delete).isTrue();
        assertThat(deletedSupport).isNull();
    }
}