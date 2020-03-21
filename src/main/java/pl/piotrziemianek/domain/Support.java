package pl.piotrziemianek.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Data

@Entity
@Table
public class Support implements Serializable {
    public Support(String support) {
        this.support = support;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String support;
}
