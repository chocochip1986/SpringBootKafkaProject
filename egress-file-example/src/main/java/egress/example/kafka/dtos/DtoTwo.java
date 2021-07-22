package egress.example.kafka.dtos;

import egress.example.kafka.entities.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtoTwo {
    private File file;
    private int index;
    private int size;
}
