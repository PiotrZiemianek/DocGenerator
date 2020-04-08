package pl.piotrziemianek.util;

import pl.piotrziemianek.domain.Patient;
import pl.piotrziemianek.domain.TherapiesCard;
import pl.piotrziemianek.domain.Therapist;

public class PreTCard {
    private Therapist therapist;
    private Patient patient;
    private TherapiesCard therapiesCard;

    public PreTCard(Therapist therapist, Patient patient, TherapiesCard therapiesCard) {
        this.therapist = therapist;
        this.patient = patient;
        this.therapiesCard = therapiesCard;
    }

    public Therapist getTherapist() {
        return therapist;
    }

    public Patient getPatient() {
        return patient;
    }

    public TherapiesCard getTherapiesCard() {
        return therapiesCard;
    }
}
