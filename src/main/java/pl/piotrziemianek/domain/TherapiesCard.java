package pl.piotrziemianek.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

    @Column
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

    /**
     * Delete patient in <i>this</i> TherapiesCard object but not delete <i>this</i> from patient TherapiesCardList.
     */
    public void deletePatient() {
        patient = null;
    }

    /**
     * Delete therapist in <i>this</i> TherapiesCard object but do not delete <i>this</i> from therapist TherapiesCardList.
     */
    public void deleteTherapist() {
        therapist = null;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TherapiesCard that = (TherapiesCard) o;

        if (id != that.id) return false;
        return yearMonth != null ? yearMonth.equals(that.yearMonth) : that.yearMonth == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (yearMonth != null ? yearMonth.hashCode() : 0);
        return result;
    }

    public String getYearMonthString() {
        return YearMonth.from(yearMonth).toString();
    }

    @Override
    public String toString() {
        YearMonth yearMonth = YearMonth.from(this.yearMonth);
        if (patient != null) {
            return yearMonth.toString() + " " + patient;
        } else {
            return yearMonth.toString();
        }
    }
}
