package business;

import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import projectExceptions.WrongCredentialsException;
import utils.AuthUtil;
import utils.HibernateUtil;

public class AuthenticationBusiness {

    public static String newUser(String username, String password) {
        Session session = HibernateUtil.openSession();

        Query query = session.createQuery("from User where username =:username ")
                .setParameter("username", username);
        User exists = (User) query.uniqueResult();
        if (exists != null) {
            return "The provided username is already in use";
        }

        Transaction transaction = session.beginTransaction();

        try {
            User newUser = new User(username, password);
            session.persist(newUser);
            transaction.commit();

            return "New user created";
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public static String login(String username, String password) throws WrongCredentialsException {
        Session session = HibernateUtil.openSession();

        Query query = session.createQuery("from User where username=:username " +
                        "and password=:password ")
                        .setParameter("username", username)
                        .setParameter("password", password);
        User exists = (User) query.uniqueResult();
        if (exists == null) {
            throw new WrongCredentialsException("Invalid credentials");
        }

        return AuthUtil.getInstance().newRandomHexString(username);
    }
}
