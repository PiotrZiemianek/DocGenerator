package pl.piotrziemianek;

import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.Therapist;

public class Main {
    public static void main(String[] args) {
        Patient patient5 = Patient.builder()
                .firstName("Michał")
                .lastName("Polak")
                .build();

        Therapist therapist1 = Therapist.builder()
                .academicDegree("mgr")
                .firstName("Katarzyna")
                .lastName("Dunag")
                .build();

        patient5.addTherapist(therapist1);

        patient5.getTherapists().forEach(System.out::println);
        therapist1.getPatients().forEach(System.out::println);
    }
}
