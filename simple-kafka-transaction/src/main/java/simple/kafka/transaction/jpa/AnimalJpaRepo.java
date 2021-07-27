package simple.kafka.transaction.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AnimalJpaRepo extends JpaRepository<AnimalEntity, Long> {
//    @Transactional
    @Override
    <S extends AnimalEntity> List<S> saveAll(Iterable<S> iterable);

//    @Transactional
    @Override
    <S extends AnimalEntity> S save(S s);
}
