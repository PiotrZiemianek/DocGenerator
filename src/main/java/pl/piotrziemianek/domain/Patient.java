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
@UniqueConstraint(columnNames = {"firstName", "lastName"}))
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToMany(mappedBy = "patients", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Therapist> therapists = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<TherapiesCard> therapiesCardList = new ArrayList<>();

    @Builder
    public Patient(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addTherapist(Therapist therapist) {
        boolean add = therapists.add(therapist);
        if (add) {
            therapist.getPatients().add(this);
        }

    }

    public void removeTherapist(Therapist therapist) {
        boolean remove = therapists.remove(therapist);
        if (remove) {
            therapist.getPatients().remove(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Patient patient = (Patient) o;

        if (id != patient.id) return false;
        if (!firstName.equals(patient.firstName)) return false;
        return lastName.equals(patient.lastName);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}
