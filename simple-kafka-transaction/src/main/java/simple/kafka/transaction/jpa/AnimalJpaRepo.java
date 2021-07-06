package simple.kafka.transaction.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalJpaRepo extends JpaRepository<AnimalEntity, Long> {
}
