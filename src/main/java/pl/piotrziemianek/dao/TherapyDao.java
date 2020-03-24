package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pl.piotrziemianek.domain.TherapiesCard;
import pl.piotrziemianek.domain.Therapy;
import pl.piotrziemianek.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TherapyDao implements CrudAccessible<Therapy> {
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<Therapy> findAll() {
        Session session = sessionFactory.openSession();
        List<Therapy> therapies = session.createQuery("from Therapy", Therapy.class).list();
        session.close();
        return therapies;
    }

    @Override
    public Optional<Therapy> findById(int id) {
        Session session = sessionFactory.openSession();
        Therapy therapy = session.get(Therapy.class, id);
        session.close();

        return Optional.ofNullable(therapy);
    }

    @Override
    public int create(Therapy entity) {
        runInTransaction(session -> {
            if (entity.getTherapiesCard() != null) {
                session.saveOrUpdate(entity.getTherapiesCard());
            }
            entity.getSubjects().forEach(session::saveOrUpdate);
            entity.getSupports().forEach(session::saveOrUpdate);
            session.save(entity);
        });
        return entity.getId();
    }

    @Override
    public int update(Therapy entity) {
        runInTransaction(session -> {
            if (entity.getTherapiesCard() != null) {
                session.saveOrUpdate(entity.getTherapiesCard());
            }
            entity.getSubjects().forEach(session::saveOrUpdate);
            entity.getSupports().forEach(session::saveOrUpdate);
            session.update(entity);

        });

        return entity.getId();
    }

    @Override
    public int createOrUpdate(Therapy entity) {
        runInTransaction(session -> {
            if (entity.getTherapiesCard() != null) {
                session.saveOrUpdate(entity.getTherapiesCard());
            }
            entity.getSubjects().forEach(session::saveOrUpdate);
            entity.getSupports().forEach(session::saveOrUpdate);
            session.saveOrUpdate(entity);

        });

        return entity.getId();
    }

    @Override
    public boolean delete(int id) {

        return runInTransaction(session -> {
            Therapy therapy = session.get(Therapy.class, id);
            if (therapy.getTherapiesCard() != null) {
                therapy.getTherapiesCard().removeTherapy(therapy);
            }
            session.delete(therapy);
        });
    }

    @Override
    public boolean deleteAll() {
        return runInTransaction(session -> {
            List<Therapy> therapies = session.createQuery("from Therapy", Therapy.class).list();
            therapies.forEach(therapy -> {
                if (therapy.getTherapiesCard() != null) {
                    therapy.getTherapiesCard().removeTherapy(therapy);
                }
                session.delete(therapy);
            });
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
            System.out.println(e.getClass() + " Message " + e.getMessage());
        }

        session.close();
        return isSuccessful;
    }
}
