package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PatientDao implements CrudAccessible<Patient> {
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<Patient> findAll() {
        Session session = sessionFactory.openSession();

        List<Patient> patients = session.createQuery("from Patient", Patient.class).list();

        session.close();
        return patients;
    }

    @Override
    public Optional<Patient> findById(int id) {
        Session session = sessionFactory.openSession();
        Patient patient = session.get(Patient.class, id);
        session.close();
        return Optional.ofNullable(patient);
    }

    @Override
    public int create(Patient entity) {
        runInTransaction(session -> {
            session.save(entity);
        });
        return entity.getId();
    }

    @Override
    public int update(Patient entity) {
        return 0;
    }

    @Override
    public int createOrUpdate(Patient entity) {
        return 0;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }

    protected void setSessionFactoryOnlyForTests(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private void runInTransaction(Consumer<Session> action) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            action.accept(session);

            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }

        session.close();
    }
}
