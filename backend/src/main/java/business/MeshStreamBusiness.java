package business;

import model.MediaMetadata;
import model.MeshStreamAvailability;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.AuthUtil;
import utils.HibernateUtil;

import java.util.Collections;
import java.util.List;

public class MeshStreamBusiness {

    public static void signalStreamAvailability(int mediaId, String clientIP) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = session.beginTransaction();

        MeshStreamAvailability toInsert = new MeshStreamAvailability(mediaId, clientIP);
        try {
            session.persist(toInsert);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public static void signalStreamUnavailability(int mediaId, String clientIP) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("from MeshStreamAvailability where " +
                        "mediaId=:mediaId " +
                        "and clientIP=:clientIP")
                .setParameter("mediaId", mediaId)
                .setParameter("clientIP", clientIP);
        MeshStreamAvailability toDelete = (MeshStreamAvailability) query.uniqueResult();
        session.delete(toDelete);

        transaction.commit();
        session.close();
    }

    public static List<String> getStreamingSources(int mediaId) {
        Session session = HibernateUtil.openSession();

        Query query = session.createQuery("select clientIP " +
                        " from MeshStreamAvailability " +
                        " where mediaId=:mediaId ")
                        .setParameter("mediaId", mediaId);
        List<String> sources = query.list();
        session.close();

        Collections.shuffle(sources);

        return sources;
    }

}
