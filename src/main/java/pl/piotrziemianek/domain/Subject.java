package pl.piotrziemianek.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Data

@Entity
@Table
public class Subject implements Serializable {
    public Subject(String subject) {
        this.subject = subject;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String subject;

}
