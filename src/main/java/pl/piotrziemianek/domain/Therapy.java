package pl.piotrziemianek.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table
public class Therapy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private LocalDate therapyDate;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Support> supports = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Therapy therapy = (Therapy) o;

        if (id != therapy.id) return false;
        return therapyDate != null ? therapyDate.equals(therapy.therapyDate) : therapy.therapyDate == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (therapyDate != null ? therapyDate.hashCode() : 0);
        return result;
    }
}
