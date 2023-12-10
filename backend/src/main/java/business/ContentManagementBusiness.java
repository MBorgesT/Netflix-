package business;

import exceptions.EmptyParameterException;
import exceptions.FolderDoesNotExistException;
import model.ChunkHash;
import model.MediaMetadata;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.HLSPackager;
import utils.HibernateUtil;
import utils.LocalPaths;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContentManagementBusiness {

    // ----------------------------------------------------------------
    // ------------------------ PUBLIC METHODS ------------------------
    // ----------------------------------------------------------------

    public static MediaMetadata getMediaById(int mediaId) {
        Session session = HibernateUtil.openSession();
        Query query = session.createQuery("from MediaMetadata where id =:id")
                .setParameter("id", mediaId);
        MediaMetadata media = (MediaMetadata) query.uniqueResult();
        session.close();
        return media;
    }

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
        // TODO: check if there is no other folder with same name
        String folderName = getFolderName(title);

        HLSPackager packager = HLSPackager.getInstance();
        if (packager.isVideoAlreadyBeingPackaged(folderName)) {
            throw new FileAlreadyExistsException(title + " is already being uploaded.");
        }
        String folderPath = LocalPaths.MEDIA_FOLDER + folderName + "/";
        if (Files.exists(Path.of(folderPath))) {
            throw new FileAlreadyExistsException(folderName + " already exists");
        }

        try {
            saveFile(fileInputStream, folderName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MediaMetadata metadata = saveMediaMetadata(title, description, folderName);
        packager.packageVideo(folderName, new HLSPackager.OnVideoProcessedListener() {
            @Override
            public void onVideoProcessed() {
                Session session = HibernateUtil.openSession();
                Transaction transaction = session.beginTransaction();
                try {
                    HLSPackager packager = HLSPackager.getInstance();
                    packager.hashChunks(folderPath, metadata.getId(), session);
//                    packager.createFinishedFlag(folderPath);

                    metadata.setUploadStatus(MediaMetadata.UploadStatus.FINISHED);
                    session.update(metadata);

                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    deleteMediaFolder(folderName);

                    Transaction errorTransaction = session.beginTransaction();
                    metadata.setUploadStatus(MediaMetadata.UploadStatus.ERROR);
                    session.update(metadata);
                    errorTransaction.commit();

                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static List<MediaMetadata> getMediaMetadataList() throws IOException {
        Session session = HibernateUtil.openSession();

        Query query = session.createQuery("from MediaMetadata ");
        List<MediaMetadata> mediaMetadatas = query.list();
        session.close();

        //mediaMetadatas = setMediaStatuses(mediaMetadatas);

        return mediaMetadatas;
    }

    public static List<MediaMetadata> getMediasReadyToPlay() throws IOException {
        List<MediaMetadata> readyMedias = new ArrayList<>();
        for (MediaMetadata mm : getMediaMetadataList()) {
            if (mm.getUploadStatus() == MediaMetadata.UploadStatus.FINISHED) {
                readyMedias.add(mm);
            }
        }
        return readyMedias;
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
        // TODO: stop process if processing
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


    // TODO: store this info at the nosql db on upload and retrieve from there
    public static List<String> getChunkUris(int mediaId) {
        MediaMetadata media = getMediaById(mediaId);
        File folder = new File(LocalPaths.MEDIA_FOLDER + media.getFolderName() + "/");
        File[] files = folder.listFiles();

        ArrayList<String> uris = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && (file.getName().endsWith(".ts") || file.getName().endsWith(".m3u8"))) {
                    uris.add(file.getName());
                }
            }
        }
        return uris;
    }

    // -----------------------------------------------------------------
    // ------------------------ PRIVATE METHODS ------------------------
    // -----------------------------------------------------------------

    private static void deleteMediaFiles(String folderName) throws FolderDoesNotExistException {
        File directory = new File(LocalPaths.MEDIA_FOLDER + folderName + "/");

        // Delete the folder and its contents recursively
        if (directory.exists()) {
            deleteMediaFolder(folderName);
        }
    }

    private static void deleteMediaFolder(String folderName) {
        File folder = new File(LocalPaths.MEDIA_FOLDER + folderName + "/");
        File[] contents = folder.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory()) {
                    deleteMediaFolder(folderName);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }

    // TODO: create an util class to deal with file management
    private static MediaMetadata saveMediaMetadata(String title, String description, String folderName) {
        MediaMetadata metadata;
        if (description == null) {
            metadata = new MediaMetadata(title, folderName, MediaMetadata.UploadStatus.PROCESSING);
        } else {
            metadata = new MediaMetadata(title, description, folderName, MediaMetadata.UploadStatus.PROCESSING);
        }

        Session session = HibernateUtil.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.persist(metadata);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            throw new RuntimeException("Error persisting media metadata");
        }
        return metadata;
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
