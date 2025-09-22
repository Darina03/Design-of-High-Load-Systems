package daryna.lab01.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Counter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private int incrementingValue;

}
