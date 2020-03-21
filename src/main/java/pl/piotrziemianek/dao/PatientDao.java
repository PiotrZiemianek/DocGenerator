package pl.piotrziemianek.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.TherapiesCard;
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
            entity.getTherapiesCardList().forEach(session::saveOrUpdate);
            entity.getTherapists().forEach(session::saveOrUpdate);
            session.save(entity);
        });
        return entity.getId();
    }

    @Override
    public int update(Patient entity) {
        runInTransaction(session -> {
            entity.getTherapiesCardList().forEach(session::saveOrUpdate);
            entity.getTherapists().forEach(session::saveOrUpdate);
            session.update(entity);
        });
        return entity.getId();
    }

    @Override
    public int createOrUpdate(Patient entity) {
        runInTransaction(session -> {
            entity.getTherapiesCardList().forEach(session::saveOrUpdate);
            entity.getTherapists().forEach(session::saveOrUpdate);
            session.saveOrUpdate(entity);
        });
        return entity.getId();
    }

    @Override
    public boolean delete(int id) {

        return runInTransaction(session -> {
            Patient patient = session.get(Patient.class, id);
            if (patient != null) {
                patient.getTherapists().forEach(therapist -> therapist
                        .getPatients()
                        .remove(patient));
                patient.getTherapiesCardList().forEach(TherapiesCard::deletePatient);
                session.delete(patient);
            }
        });
    }

    @Override
    public boolean deleteAll() {
        return runInTransaction(session -> {
            List<Patient> patients = session.createQuery("from Patient", Patient.class).list();
            patients.forEach(patient -> {
                patient.getTherapists().forEach(therapist -> therapist
                        .getPatients()
                        .remove(patient));
                patient.getTherapiesCardList().forEach(TherapiesCard::deletePatient);
                session.delete(patient);
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
        }

        session.close();
        return isSuccessful;
    }

}
