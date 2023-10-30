package business;

import jakarta.ws.rs.WebApplicationException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import tools.HLSPackager;
import tools.LocalPaths;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class ContentManagementBusiness {

    // ----------------------------------------------------------------
    // ------------------------ PUBLIC METHODS ------------------------
    // ----------------------------------------------------------------

    public static void uploadMedia(
            InputStream fileInputStream,
            FormDataContentDisposition fileMetaData
    ) throws Exception {
        String[] fileInfo = getFileInfo(fileMetaData);
        String fileName = fileInfo[0];
        String fileExtension = fileInfo[1];

        HLSPackager packager = HLSPackager.getInstance();
        if (packager.isVideoAlreadyBeingPackaged(fileName)) {
            throw new FileAlreadyExistsException(fileName + " is already being uploaded.");
        }
        if (Files.exists(Path.of(LocalPaths.MEDIA_FOLDER + fileName + "/"))) {
            throw new FileAlreadyExistsException(fileName + " already exists");
        }

        try {
            saveFile(fileInputStream, fileName, fileExtension);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        packager.packageVideo(fileName);
    }

    public static HashMap<String, String> getUploadStatuses() throws IOException {
        return HLSPackager.getInstance().getUploadStatuses();
    }

    // -----------------------------------------------------------------
    // ------------------------ PRIVATE METHODS ------------------------
    // -----------------------------------------------------------------

    private static String[] getFileInfo(FormDataContentDisposition fileMetaData) {
        String file_name = fileMetaData.getFileName();
        String file_extension = "";
        if (file_name.contains(".")) {
            String reversed = String.valueOf(new StringBuilder(file_name).reverse());
            file_extension = reversed.split("\\.")[0];
            file_extension = "." + String.valueOf(new StringBuilder(file_extension).reverse());

            file_name = String.valueOf(new StringBuilder(reversed.split("\\.")[1]).reverse());
        }

        return new String[]{file_name, file_extension};
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
