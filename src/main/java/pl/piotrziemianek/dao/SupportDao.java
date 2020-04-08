package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.piotrziemianek.domain.Support;
import pl.piotrziemianek.domain.Support;
import pl.piotrziemianek.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SupportDao implements CrudAccessible<Support> {
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<Support> findAll() {
        Session session = sessionFactory.openSession();
        List<Support> supports = session.createQuery("from Support", Support.class).list();
        session.close();
        return supports;
    }

    @Override
    public Optional<Support> findById(int id) {
        Session session = sessionFactory.openSession();
        Support support = session.get(Support.class, id);
        session.close();
        return Optional.ofNullable(support);
    }

    @Override
    public int create(Support entity) {
        boolean isSuccessful = runInTransaction(session -> session.save(entity));
        if (!isSuccessful) {
            entity.setId(-1);
        }
        return entity.getId();
    }

    @Override
    public int update(Support entity) {
        boolean isSuccessful = runInTransaction(session -> session.update(entity));
        if (!isSuccessful) {
            entity.setId(-1);
        }
        return entity.getId();
    }

    @Override
    public int createOrUpdate(Support entity) {
        boolean isSuccessful = runInTransaction(session -> session.saveOrUpdate(entity));
        if (!isSuccessful) {
            entity.setId(-1);
        }
        return entity.getId();
    }

    @Override
    public boolean delete(int id) {
        return runInTransaction(session -> {
            Support support = session.get(Support.class, id);
            session.delete(support);
        });
    }

    /**
     * It works only when all supports has no connection with any Therapy object.
     *
     * @return true if delete was successful.
     */
    @Override
    public boolean deleteAll() {
        return runInTransaction(session -> {
            List<Support> supports = session.createQuery("from Support", Support.class).list();
            supports.forEach(session::delete);
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
