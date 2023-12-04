package business;

import exceptions.EmptyParameterException;
import exceptions.FolderDoesNotExistException;
import exceptions.InvalidRoleException;
import model.MediaMetadata;
import model.User;
import org.apache.commons.lang3.EnumUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.AuthUtil;
import utils.HLSPackager;
import utils.HibernateUtil;
import utils.LocalPaths;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;

public class ContentManagementBusiness {

    // ----------------------------------------------------------------
    // ------------------------ PUBLIC METHODS ------------------------
    // ----------------------------------------------------------------

    public static boolean doesTitleExist(String title) {
        Session session = HibernateUtil.openSession();
        Query query = session.createQuery("from MediaMetadata where title =:title")
                .setParameter("title", title);
        MediaMetadata exists = (MediaMetadata) query.uniqueResult();
        session.close();
        return exists != null;
    }

    public static boolean doesTitleExistAtAnotherId(String title, int id) {
        Session session = HibernateUtil.openSession();
        Query query = session.createQuery("from MediaMetadata where title =:title " +
                        "and id !=:id")
                .setParameter("title", title)
                .setParameter("id", id);
        MediaMetadata exists = (MediaMetadata) query.uniqueResult();
        session.close();
        return exists != null;
    }

    public static void uploadMedia(
            String title, String description,
            InputStream fileInputStream
    ) throws Exception {
        String folderName = getFolderName(title);

        HLSPackager packager = HLSPackager.getInstance();
        if (packager.isVideoAlreadyBeingPackaged(folderName)) {
            throw new FileAlreadyExistsException(title + " is already being uploaded.");
        }
        if (Files.exists(Path.of(LocalPaths.MEDIA_FOLDER + folderName + "/"))) {
            throw new FileAlreadyExistsException(folderName + " already exists");
        }

        try {
            saveFile(fileInputStream, folderName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        saveMediaMetadata(title, description, folderName);
        packager.packageVideo(folderName);
    }

    public static List<MediaMetadata> getMediaMetadataList() throws IOException {
        Session session = HibernateUtil.openSession();

        Query query = session.createQuery("from MediaMetadata ");
        List<MediaMetadata> mediaMetadatas = query.list();
        session.close();

        Map<String, MediaMetadata.UploadStatus> statuses = HLSPackager.getInstance().getUploadStatuses();
        String folderName;
        for (MediaMetadata mm : mediaMetadatas) {
            folderName = mm.getFolderName();
            if (statuses.containsKey(folderName)) {
                mm.setUploadStatus(statuses.get(folderName));
            } else {
                if (checkIfProcessingFinished(folderName)) {
                    mm.setUploadStatus(MediaMetadata.UploadStatus.FINISHED);
                } else {
                    mm.setUploadStatus(MediaMetadata.UploadStatus.ERROR);
                }
            }
        }

        return mediaMetadatas;
    }

    // TODO: create this kind of methods in abstract
    public static boolean doesMediaExist(int mediaId) {
        Session session = HibernateUtil.openSession();
        Query query = session.createQuery("from MediaMetadata where id =:id ")
                .setParameter("id", mediaId);
        MediaMetadata exists = (MediaMetadata) query.uniqueResult();
        session.close();
        return exists != null;
    }

    public static void updateMedia(int id, String title, String description) throws EmptyParameterException {
        if (title.isEmpty()) {
            throw new EmptyParameterException();
        }

        Session session = HibernateUtil.openSession();
        Transaction transaction = session.beginTransaction();

        MediaMetadata toUpdate = session.get(MediaMetadata.class, id);
        toUpdate.setTitle(title);
        toUpdate.setDescription(description);
        try {
            session.update(toUpdate);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public static void deleteMedia(int mediaId) {
        Session session = HibernateUtil.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            MediaMetadata toDelete = session.get(MediaMetadata.class, mediaId);
            deleteMediaFiles(toDelete.getFolderName());

            session.delete(toDelete);

            transaction.commit();
            session.close();
        } catch (FolderDoesNotExistException e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------------------
    // ------------------------ PRIVATE METHODS ------------------------
    // -----------------------------------------------------------------

    private static void deleteMediaFiles(String folderName) throws FolderDoesNotExistException {
        File directory = new File(LocalPaths.MEDIA_FOLDER + folderName + "/");

        // Delete the folder and its contents recursively
        if (directory.exists()) {
            deleteFolder(directory);
        }
//        } else {
//            throw new FolderDoesNotExistException("Folder does not exist");
//        }
    }

    private static void deleteFolder(File folder) {
        File[] contents = folder.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    // TODO: create an util class to deal with file management
    private static void saveMediaMetadata(String title, String description, String folderName) {
        MediaMetadata metadata;
        if (description == null) {
            metadata = new MediaMetadata(title, folderName);
        } else {
            metadata = new MediaMetadata(title, description, folderName);
        }

        Session session = HibernateUtil.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(metadata);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private static String getFolderName(String title) {
        // Remove accents and replaces spaces with underscores
        String normalizedString = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll(" ", "_");

        // Keep only alphanumeric characters
        return normalizedString.replaceAll("[^a-zA-Z0-9]", "");
    }

    private static void saveFile(InputStream fileInputStream,
                                 String folderName) throws IOException {
        String UPLOAD_PATH = LocalPaths.MEDIA_FOLDER + folderName + "/";
        try {
            byte[] bytes = new byte[1024];

            Files.createDirectory(Path.of(UPLOAD_PATH));

            OutputStream out = new FileOutputStream(UPLOAD_PATH + "original");
            int read = 0;
            while ((read = fileInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            throw e;
        }
    }

    private static boolean checkIfProcessingFinished(String folderName) {
        String filePath = LocalPaths.MEDIA_FOLDER + folderName + "/flag.txt";
        File file = new File(filePath);
        return file.exists();
    }
}
