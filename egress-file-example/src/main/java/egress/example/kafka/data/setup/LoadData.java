package egress.example.kafka.data.setup;

import com.github.javafaker.Faker;
import egress.example.kafka.entities.Animal;
import egress.example.kafka.enums.Status;
import egress.example.kafka.jpa.AnimalJpaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LoadData implements ApplicationRunner {
    @Autowired private AnimalJpaRepo animalJpaRepo;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Faker faker = new Faker();
        for ( int i = 0 ; i < 100 ; i++) {
            animalJpaRepo.saveAll(gen(faker));
        }
    }

    private List<Animal> gen(Faker faker) {
        List<Animal> animals = new ArrayList<>();
        for ( int i = 0 ; i < 1000 ; i++) {
            animals.add(Animal.builder().name(faker.animal().name()).status(Status.NEW).build());
        }
        return animals;
    }
}
