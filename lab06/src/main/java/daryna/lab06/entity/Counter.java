package daryna.lab06.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "counters")
public class Counter implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private int incrementingValue;
}
