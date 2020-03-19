package pl.piotrziemianek.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor

@Entity
@Table(uniqueConstraints=
@UniqueConstraint(columnNames={"academicDegree","firstName","lastName"}))
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

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "therapist_patient", joinColumns = @JoinColumn(name = "therapist_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id"))
    private Set<Patient> patients = new HashSet<>();

    @OneToMany(mappedBy = "therapist", cascade = CascadeType.PERSIST)
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
}
