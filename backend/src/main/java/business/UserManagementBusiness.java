package business;

import model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.json.JSONObject;
import utils.HibernateUtil;

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

}
