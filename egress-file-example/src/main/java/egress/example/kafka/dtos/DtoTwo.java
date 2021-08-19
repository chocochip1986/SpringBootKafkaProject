package egress.example.kafka.dtos;

import egress.example.kafka.entities.Animal;
import egress.example.kafka.entities.File;
import egress.example.kafka.pojos.AnimalAggregateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtoTwo {
    private File file;
    private List<Animal> animals;
}
