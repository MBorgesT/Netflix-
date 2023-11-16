package business;

import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.AuthUtil;
import utils.HibernateUtil;

import java.security.NoSuchAlgorithmException;

public class AuthenticationBusiness {

    public static String newUser(String username, String password, boolean isAdmin) throws NoSuchAlgorithmException {
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
            User newUser = new User(username, hashedPassword,
                    isAdmin ? User.Role.ADMIN : User.Role.SUBSCRIBER);
            session.persist(newUser);
            transaction.commit();

            return "New " + (isAdmin ? "admin" : "subscriber") + " created";
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public static boolean login(String username, String password, boolean isAdmin) throws NoSuchAlgorithmException {
        Session session = HibernateUtil.openSession();

        byte[] hashedPassword = AuthUtil.hashPassword(password);
        String role = isAdmin ? User.Role.ADMIN.toString() : User.Role.SUBSCRIBER.toString();
        Query query = session.createQuery("from User where username=:username " +
                        "and password=:password " +
                        "and role=:role")
                        .setParameter("username", username)
                        .setParameter("password", hashedPassword)
                        .setParameter("role", role);
        User exists = (User) query.uniqueResult();
        return exists != null;
    }
}
