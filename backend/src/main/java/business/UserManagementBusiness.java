package business;

import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.JSONObject;
import utils.AuthUtil;
import utils.HibernateUtil;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserManagementBusiness {

    public static List<User> getUserList(boolean isAdmin) {
        Session session = HibernateUtil.openSession();

        User.Role role = isAdmin ? User.Role.ADMIN : User.Role.SUBSCRIBER;
        Query query = session.createQuery("from User where role=:role ")
                .setParameter("role", role);
        List<User> users = query.list();
        session.close();

        return users;
    }

    public static boolean doesUserExist(String username) {
        Session session = HibernateUtil.openSession();
        Query query = session.createQuery("from User where username =:username ")
                .setParameter("username", username);
        User exists = (User) query.uniqueResult();
        session.close();
        return exists != null;
    }

    public static boolean doesUserExist(int id) {
        Session session = HibernateUtil.openSession();
        Query query = session.createQuery("from User where id =:id ")
                .setParameter("id", id);
        User exists = (User) query.uniqueResult();
        session.close();
        return exists != null;
    }

    public static boolean login(String username, String password, boolean isAdmin) throws NoSuchAlgorithmException {
        Session session = HibernateUtil.openSession();

        byte[] hashedPassword = AuthUtil.hashPassword(password);
        User.Role role = isAdmin ? User.Role.ADMIN : User.Role.SUBSCRIBER;
        Query query = session.createQuery("from User where username=:username " +
                        "and password=:password " +
                        "and role=:role")
                .setParameter("username", username)
                .setParameter("password", hashedPassword)
                .setParameter("role", role);
        User exists = (User) query.uniqueResult();
        session.close();

        return exists != null;
    }

    public static void newUser(String username, String password, boolean isAdmin) throws NoSuchAlgorithmException {
        Session session = HibernateUtil.openSession();
        Transaction transaction = session.beginTransaction();

        byte[] hashedPassword = AuthUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword,
                isAdmin ? User.Role.ADMIN : User.Role.SUBSCRIBER);
        try {
            session.persist(newUser);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public static void deleteUser(int id) {
        Session session = HibernateUtil.openSession();
        User toDelete = session.get(User.class, id);
        session.delete(toDelete);
        session.getTransaction().commit();
        session.close();
    }

}
