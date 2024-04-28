package restaurant_business_system.db;

import java.util.List;

import javax.management.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import io.dropwizard.hibernate.AbstractDAO;

public class CheckID {
    private SessionFactory sessionFactory;

    public CheckID(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public boolean checkIDExist(String id, String table) {
        //Check in database if the ID exists
        // try (Session session = sessionFactory.openSession()) {
        //     String sql = "SELECT * FROM " + table + " WHERE id = :id";
        //     Query query = (Query) session.createNativeQuery(sql); 
        //     query.setParameter("id", id);
        //     List result = query.list();
        //     return !result.isEmpty();
        // }
        return false;
    }
}
