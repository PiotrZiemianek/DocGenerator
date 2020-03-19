package pl.piotrziemianek.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor

@Entity
@Table(uniqueConstraints=
@UniqueConstraint(columnNames={"firstName","lastName"}))
public class Patient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToMany(mappedBy = "patients", cascade = CascadeType.PERSIST)
    private Set<Therapist> therapists;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.PERSIST)
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
}
