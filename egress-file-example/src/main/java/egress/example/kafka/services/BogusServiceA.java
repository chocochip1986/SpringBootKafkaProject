package egress.example.kafka.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BogusServiceA {
    @Autowired private BogusServiceB bogusServiceB;
    public void doIt() {
        Long id = bogusServiceB.createNew();
        bogusServiceB.update(id, 500);
        bogusServiceB.update(id, 500);
    }
}
