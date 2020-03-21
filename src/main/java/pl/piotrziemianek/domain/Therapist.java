package pl.piotrziemianek.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"academicDegree", "firstName", "lastName"}))
public class Therapist implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String academicDegree;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "therapist_patient", joinColumns = @JoinColumn(name = "therapist_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id"))
    private Set<Patient> patients = new HashSet<>();

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<TherapiesCard> therapiesCardList = new ArrayList<>();

    @Builder
    public Therapist(String academicDegree, String firstName, String lastName) {
        this.academicDegree = academicDegree;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addPatient(Patient patient) {
        boolean add = patients.add(patient);
        if (add) {
            patient.getTherapists().add(this);
        }
    }

    public void removePatient(Patient patient) {
        boolean remove = patients.remove(patient);
        if (remove) {
            patient.getTherapists().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Therapist therapist = (Therapist) o;

        if (id != therapist.id) return false;
        if (academicDegree != null ? !academicDegree.equals(therapist.academicDegree) : therapist.academicDegree != null)
            return false;
        if (!firstName.equals(therapist.firstName)) return false;
        return lastName.equals(therapist.lastName);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (academicDegree != null ? academicDegree.hashCode() : 0);
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}
