package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.piotrziemianek.domain.Subject;
import pl.piotrziemianek.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class SubjectDao implements CrudAccessible<Subject> {

    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<Subject> findAll() {
        Session session = sessionFactory.openSession();
        List<Subject> subjects = session.createQuery("from Subject", Subject.class).list();
        session.close();
        return subjects;
    }

    @Override
    public Optional<Subject> findById(int id) {
        Session session = sessionFactory.openSession();
        Subject subject = session.get(Subject.class, id);
        session.close();
        return Optional.ofNullable(subject);
    }

    @Override
    public int create(Subject entity) {
        runInTransaction(session -> session.save(entity));
        return entity.getId();
    }

    @Override
    public int update(Subject entity) {
        runInTransaction(session -> session.update(entity));
        return entity.getId();
    }

    @Override
    public int createOrUpdate(Subject entity) {
        runInTransaction(session -> session.saveOrUpdate(entity));
        return entity.getId();
    }

    @Override
    public boolean delete(int id) {
        return runInTransaction(session -> {
            Subject subject = session.get(Subject.class, id);
            session.delete(subject);
        });
    }

    /**
     * It works only when all subjects has no connection with any Therapy object.
     *
     * @return true if delete was successful.
     */
    @Override
    public boolean deleteAll() {
        return runInTransaction(session -> {
            List<Subject> subjects = session.createQuery("from Subject", Subject.class).list();
            subjects.forEach(session::delete);
        });
    }

    protected void setSessionFactoryOnlyForTests(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private boolean runInTransaction(Consumer<Session> action) {
        boolean isSuccessful = false;
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            action.accept(session);

            transaction.commit();
            isSuccessful = true;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }

        session.close();
        return isSuccessful;
    }
}
