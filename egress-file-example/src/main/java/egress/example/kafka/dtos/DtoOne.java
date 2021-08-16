package egress.example.kafka.dtos;

import egress.example.kafka.entities.File;
import egress.example.kafka.pojos.AnimalAggregateResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class DtoOne {
    private String uuid;
    private File file;
    private AnimalAggregateResult result;

    public DtoOne() {
        this.uuid = UUID.randomUUID().toString();
    }
}
