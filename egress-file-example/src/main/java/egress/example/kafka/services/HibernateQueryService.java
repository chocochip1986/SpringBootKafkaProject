package egress.example.kafka.services;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HibernateQueryService {
    @Autowired private SessionFactory sessionFactory;

    public <T> List<T> selectQueryReturnList(String queryString, Class<T> resultKlazz) {
        Session session = sessionFactory.openSession();
        Query<T> query = session.createQuery(queryString, resultKlazz);

        List<T> results = query.getResultList();

        session.close();
        return results;
    }

    public <T> T selectQueryReturnPojo(String queryString, Class<T> resultKlazz) {
        Session session = sessionFactory.openSession();
        Query<T> query = session.createQuery(queryString, resultKlazz);

        T results = query.getSingleResult();

        session.close();
        return results;
    }
}
