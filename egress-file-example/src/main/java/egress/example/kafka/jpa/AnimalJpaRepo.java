package egress.example.kafka.jpa;

import egress.example.kafka.entities.Animal;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalJpaRepo extends JpaRepository<Animal, Long> {

    @Query("SELECT a FROM Animal a ORDER BY a.id DESC")
    Optional<List<Animal>> findByIdWithPageable(Pageable pageable);

    @Query("SELECT COUNT(a) FROM Animal a")
    int countAll();
}
