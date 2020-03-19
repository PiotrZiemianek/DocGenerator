package pl.piotrziemianek.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor

@Entity
@Table
public class TherapiesCard implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(nullable = false)
    private LocalDate yearMonth;

    @OneToMany(mappedBy = "therapiesCard", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Therapy> therapies = new ArrayList<>();

    public TherapiesCard(LocalDate yearMonth) {
        this.yearMonth = yearMonth;
    }

    public void setTherapist(Therapist therapist) {
        if (this.therapist != null) {
            this.therapist.getTherapiesCardList().remove(this);
        }
        if (therapist != null) {
            this.therapist = therapist;
            therapist.getTherapiesCardList().add(this);
        } else {
            this.patient = null;
        }
    }

    public void setPatient(Patient patient) {
        if (this.patient != null) {
            this.patient.getTherapiesCardList().remove(this);
        }
        if (patient != null) {
            this.patient = patient;
            patient.getTherapiesCardList().add(this);
        } else {
            this.patient = null;
        }
    }
    public void deletePatient(){
        setPatient(null);
    }
    public void deleteTherapist(){
        setTherapist(null);
    }

    public void addTherapy(Therapy therapy) {
        therapies.add(therapy);
        therapy.setTherapiesCard(this);
    }

    public void removeTherapy(Therapy therapy) {
        boolean remove = therapies.remove(therapy);
        if (remove) {
            therapy.setTherapiesCard(null);
        }
    }

    @Override
    public String toString() {
        return "TherapiesCard{" +
                "ID= " + id +
                ", therapist= " + therapist.getFirstName() + therapist.getLastName() +
                ", patient= " + patient.getFirstName() + therapist.getLastName() +
                '}';
    }
}
