package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pl.piotrziemianek.domain.TherapiesCard;
import pl.piotrziemianek.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TherapiesCardDao implements CrudAccessible<TherapiesCard> {
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<TherapiesCard> findAll() {
        Session session = sessionFactory.openSession();
        List<TherapiesCard> therapiesCardList = session.createQuery("from TherapiesCard", TherapiesCard.class).list();
        session.close();
        return therapiesCardList;
    }

    @Override
    public Optional<TherapiesCard> findById(int id) {
        Session session = sessionFactory.openSession();
        TherapiesCard therapiesCard = session.get(TherapiesCard.class, id);
        session.close();
        return Optional.ofNullable(therapiesCard);
    }

    @Override
    public int create(TherapiesCard entity) {
        boolean isSuccessful = runInTransaction(session -> {
            if (entity.getPatient() != null) {
                session.saveOrUpdate(entity.getPatient());
            }
            if (entity.getTherapist() != null) {
                session.saveOrUpdate(entity.getTherapist());
            }
            entity.getTherapies().forEach(session::saveOrUpdate);
            session.save(entity);
        });
        if (!isSuccessful){
            entity.setId(-1);
        }
        return entity.getId();
    }

    @Override
    public int update(TherapiesCard entity) {
        boolean isSuccessful = runInTransaction(session -> {
            if (entity.getPatient() != null) {
                session.saveOrUpdate(entity.getPatient());
            }
            if (entity.getTherapist() != null) {
                session.saveOrUpdate(entity.getTherapist());
            }
            entity.getTherapies().forEach(session::saveOrUpdate);
            session.update(entity);
        });
        if (!isSuccessful){
            entity.setId(-1);
        }
        return entity.getId();
    }

    @Override
    public int createOrUpdate(TherapiesCard entity) {
        boolean isSuccessful = runInTransaction(session -> {
            if (entity.getPatient() != null) {
                session.saveOrUpdate(entity.getPatient());
            }
            if (entity.getTherapist() != null) {
                session.saveOrUpdate(entity.getTherapist());
            }
            entity.getTherapies().forEach(session::saveOrUpdate);
            session.saveOrUpdate(entity);
        });
        if (!isSuccessful){
            entity.setId(-1);
        }
        return entity.getId();
    }

    @Override
    public boolean delete(int id) {
        return runInTransaction(session -> {
            TherapiesCard therapiesCard = session.get(TherapiesCard.class, id);
            therapiesCard.setPatient(null);
            therapiesCard.setTherapist(null);
            therapiesCard.getTherapies().forEach(therapy -> therapy.setTherapiesCard(null));
            session.delete(therapiesCard);
        });

    }

    @Override
    public boolean deleteAll() {
        return runInTransaction(session -> {
            List<TherapiesCard> therapiesCardList = session.
                    createQuery("from TherapiesCard", TherapiesCard.class).list();
            therapiesCardList.forEach(therapiesCard -> {
                therapiesCard.setPatient(null);
                therapiesCard.setTherapist(null);
                therapiesCard.getTherapies().forEach(therapy -> therapy.setTherapiesCard(null));
                session.delete(therapiesCard);
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
            System.out.println(e.getClass() + " Message: " + e.getMessage());
        }

        session.close();
        return isSuccessful;
    }
}
