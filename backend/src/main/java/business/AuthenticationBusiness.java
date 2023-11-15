package business;

import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.AuthUtil;
import utils.HibernateUtil;

import java.security.NoSuchAlgorithmException;

public class AuthenticationBusiness {

    public static String newSubscriber(String username, String password) throws NoSuchAlgorithmException {
        Session session = HibernateUtil.openSession();

        Query query = session.createQuery("from User where username =:username ")
                .setParameter("username", username);
        User exists = (User) query.uniqueResult();
        if (exists != null) {
            return "The provided username is already in use";
        }

        Transaction transaction = session.beginTransaction();

        try {
            byte[] hashedPassword = AuthUtil.hashPassword(password);
            User newUser = new User(username, hashedPassword, User.Role.SUBSCRIBER);
            session.persist(newUser);
            transaction.commit();

            return "New subscriber created";
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public static boolean subscriberLogin(String username, String password) throws NoSuchAlgorithmException {
        Session session = HibernateUtil.openSession();

        byte[] hashedPassword = AuthUtil.hashPassword(password);
        Query query = session.createQuery("from User where username=:username " +
                        "and password=:password ")
                        .setParameter("username", username)
                        .setParameter("password", hashedPassword);
        User exists = (User) query.uniqueResult();
        return exists != null;
    }
}
