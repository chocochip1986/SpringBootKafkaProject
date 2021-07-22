package egress.example.kafka.entities;

import egress.example.kafka.enums.FileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "file")
@Builder
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_seq")
    private Long id;
    private String uuid;

    @Enumerated(EnumType.STRING)
    private FileStatus status;
    private String absFilePath;
    private Integer currentCount;
    private Integer totalCount;

}
