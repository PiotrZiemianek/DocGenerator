package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.TherapiesCard;
import pl.piotrziemianek.domain.Therapist;
import pl.piotrziemianek.util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TherapistDao implements CrudAccessible<Therapist> {
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<Therapist> findAll() {
        Session session = sessionFactory.openSession();
        List<Therapist> therapists = session.createQuery("from Therapist", Therapist.class).list();
        session.close();
        return therapists;
    }

    @Override
    public Optional<Therapist> findById(int id) {
        Session session = sessionFactory.openSession();
        Therapist therapist = session.get(Therapist.class, id);
        session.close();
        return Optional.ofNullable(therapist);
    }

    @Override
    public int create(Therapist entity) {
        runInTransaction(session -> {
            entity.getTherapiesCardList().forEach(session::saveOrUpdate);
            entity.getPatients().forEach(session::saveOrUpdate);
            session.save(entity);
        });
        return entity.getId();
    }

    @Override
    public int update(Therapist entity) {
        runInTransaction(session -> {
            entity.getTherapiesCardList().forEach(session::saveOrUpdate);
            entity.getPatients().forEach(session::saveOrUpdate);
            session.update(entity);
        });
        return entity.getId();
    }

    @Override
    public int createOrUpdate(Therapist entity) {
        runInTransaction(session -> {
            entity.getTherapiesCardList().forEach(session::saveOrUpdate);
            entity.getPatients().forEach(session::saveOrUpdate);
            session.saveOrUpdate(entity);
        });
        return entity.getId();
    }

    @Override
    public boolean delete(int id) {
        return runInTransaction(session -> {
            Therapist therapist = session.get(Therapist.class, id);
            if (therapist != null) {
                therapist.getPatients().forEach(patient -> patient
                        .getTherapists()
                        .remove(therapist));
                therapist.getTherapiesCardList().forEach(TherapiesCard::deleteTherapist);
                session.delete(therapist);
            }
        });
    }

    @Override
    public boolean deleteAll() {
        return runInTransaction(session -> {
            List<Therapist> therapists = session.createQuery("from Therapist", Therapist.class).list();
            therapists.forEach(therapist -> {
                therapist.getPatients().forEach(patient -> patient
                        .getTherapists()
                        .remove(therapist));
                therapist.getTherapiesCardList().forEach(TherapiesCard::deleteTherapist);
                session.delete(therapist);
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
                System.out.println(e.getClass() + " Message " + e.getMessage());
            }
        }

        session.close();
        return isSuccessful;
    }
}
