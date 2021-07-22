package egress.example.kafka.dtos;

import egress.example.kafka.entities.Animal;
import egress.example.kafka.entities.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DtoThree {
    private File file;
    private List<Animal> animals;
}
