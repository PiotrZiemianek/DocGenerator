package pl.piotrziemianek.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor

@Entity
@Table
public class Therapy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private LocalDate therapyDate;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Support> supports = new HashSet<>();

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Subject> subjects = new HashSet<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "therapiesCard_id")
    private TherapiesCard therapiesCard;

    public Therapy(LocalDate therapyDate) {
        this.therapyDate = therapyDate;
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        subjects.remove(subject);

    }

    public void addSupport(Support support) {
        supports.add(support);
    }

    public void removeSupport(Support support) {
        supports.remove(support);

    }

}
