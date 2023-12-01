package business;

import model.MediaMetadata;
import model.User;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.HLSPackager;
import utils.HibernateUtil;
import utils.LocalPaths;

import javax.print.attribute.standard.Media;
import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentManagementBusiness {

    // ----------------------------------------------------------------
    // ------------------------ PUBLIC METHODS ------------------------
    // ----------------------------------------------------------------

    public static void uploadMedia(
            String title, String description,
            InputStream fileInputStream,
            FormDataContentDisposition fileMetaData
    ) throws Exception {
        String[] fileInfo = getFileInfo(fileMetaData);
        String folderName = fileInfo[0];
        String fileExtension = fileInfo[1];

        HLSPackager packager = HLSPackager.getInstance();
        if (packager.isVideoAlreadyBeingPackaged(folderName)) {
            throw new FileAlreadyExistsException(title + " is already being uploaded.");
        }
        if (Files.exists(Path.of(LocalPaths.MEDIA_FOLDER + folderName + "/"))) {
            throw new FileAlreadyExistsException(folderName + " already exists");
        }

        try {
            saveFile(fileInputStream, folderName, fileExtension);
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
        for (MediaMetadata mm: mediaMetadatas) {
            folderName = mm.getFolderName();
            if (statuses.containsKey(folderName)) {
                mm.setUploadStatus(statuses.get(folderName));
            }
        }

        return mediaMetadatas;
    }

//    public static HashMap<String, Media> getUploadStatuses() throws IOException {
//        return HLSPackager.getInstance().getUploadStatuses();
//    }

    // -----------------------------------------------------------------
    // ------------------------ PRIVATE METHODS ------------------------
    // -----------------------------------------------------------------

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

    private static String[] getFileInfo(FormDataContentDisposition fileMetaData) {
        String folderName = fileMetaData.getFileName();
        String fileExtension = "";
        if (folderName.contains(".")) {
            String reversed = String.valueOf(new StringBuilder(folderName).reverse());
            fileExtension = reversed.split("\\.")[0];
            fileExtension = "." + String.valueOf(new StringBuilder(fileExtension).reverse());

            folderName = String.valueOf(new StringBuilder(reversed.split("\\.")[1]).reverse());
        }

        return new String[]{folderName, fileExtension};
    }

    private static void saveFile(InputStream fileInputStream,
                                 String fileName, String fileExtension) throws IOException {
        String UPLOAD_PATH = LocalPaths.MEDIA_FOLDER + fileName + "/";
        try {
            byte[] bytes = new byte[1024];

            Files.createDirectory(Path.of(UPLOAD_PATH));

            OutputStream out = new FileOutputStream(UPLOAD_PATH + "original" + fileExtension);
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

}
