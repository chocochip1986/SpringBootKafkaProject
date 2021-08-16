package egress.example.kafka.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalAggregateResult {
    private Long minIndex;
    private Long maxIndex;
    private Long totalCount;
}
