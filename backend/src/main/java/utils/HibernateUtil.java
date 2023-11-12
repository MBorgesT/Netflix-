package utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static HibernateUtil instance;

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
        setup();
    }

    private void setup() {
        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    public static void init() {
        instance = new HibernateUtil();
    }

    public static HibernateUtil getInstance() {
        if (instance == null) {
            instance = new HibernateUtil();
        }
        return instance;
    }

    public static Session openSession() {
        return sessionFactory.openSession();
    }

}
