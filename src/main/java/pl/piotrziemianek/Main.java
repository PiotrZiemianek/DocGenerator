package pl.piotrziemianek;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import pl.piotrziemianek.domain.*;
import pl.piotrziemianek.util.HibernateUtil;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Therapist therapist = Therapist.builder()
                .academicDegree("mgr")
                .firstName("Katarzyna")
                .lastName("Ziemianek")
                .build();

        Patient patient = Patient.builder()
                .firstName("Piotr")
                .lastName("Ziemianek")
                .build();

        Therapy therapy = new Therapy(LocalDate.now());
        therapy.addSubject(new Subject("Dobry temat"));
        therapy.addSupport(new Support("Dobre wspomaganie"));

        TherapiesCard therapiesCard = new TherapiesCard(LocalDate.now());
        therapiesCard.setPatient(patient);
        therapiesCard.setTherapist(therapist);
        therapiesCard.addTherapy(therapy);

        final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(therapist);
            session.persist(patient);
            session.persist(therapy);
            session.save(therapiesCard);


            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Błąd" + e.getMessage());
        }
        List<TherapiesCard> from_therapiesCard = session.createQuery("From TherapiesCard", TherapiesCard.class).list();
        session.close();

        from_therapiesCard.forEach(System.out::println);

        session = sessionFactory.openSession();
        transaction = null;
        try {
            transaction = session.beginTransaction();

            TherapiesCard therapiesCard1 = session.get(TherapiesCard.class, 1);
//            therapiesCard1.deletePatient();
//            therapiesCard1.deleteTherapist();
            session.delete(therapiesCard1);
            System.out.println(session.get(TherapiesCard.class, 1));
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Błąd" + e.getMessage());
        }
        session.close();
    }
}
