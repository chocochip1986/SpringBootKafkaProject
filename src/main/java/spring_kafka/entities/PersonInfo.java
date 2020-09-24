package spring_kafka.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring_kafka.enums.Gender;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonInfo {
    private String firstName;
    private String lastName;
    private Gender gender;
    private int bankAccount;
}
