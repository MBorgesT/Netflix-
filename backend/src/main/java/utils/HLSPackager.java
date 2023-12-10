package utils;

import model.ChunkHash;
import model.MediaMetadata;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public final class HLSPackager {

    public interface OnVideoProcessedListener {
        void onVideoProcessed();
    }

    private static HLSPackager instance;
    private final HashMap<String, Process> processes;

    private HLSPackager() {
        processes = new HashMap<>();
    }

    public static HLSPackager getInstance() {
        if (instance == null) {
            instance = new HLSPackager();
        }
        return instance;
    }

    // ----------------------------------------------------------------
    // ------------------------ PUBLIC METHODS ------------------------
    // ----------------------------------------------------------------

    public void packageVideo(String processName, OnVideoProcessedListener listener) throws IOException {
        cleanProcesses();
        String mediaPath = LocalPaths.MEDIA_FOLDER + processName + "/";

        String[] commandList = new String[]{
                "/bin/bash", "-c", LocalPaths.PACKAGING_SCRIPT
        };
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.directory(new File(mediaPath));
        Process p = processBuilder.start();
        processes.put(processName, p);

        Thread whenFinished = new Thread(() -> {
            try {
                int result = p.waitFor();
                if (result == 0) { // success
                    listener.onVideoProcessed();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        whenFinished.start();
    }

    public boolean isVideoAlreadyBeingPackaged(String fileName) {
        cleanProcesses();

        if (!processes.containsKey(fileName)) {
            return false;
        }
        return (checkProcessStatus(fileName) == MediaMetadata.UploadStatus.PROCESSING);
    }

    public HashMap<String, MediaMetadata.UploadStatus> getUploadStatuses() throws IOException {
        cleanProcesses();

        HashMap<String, MediaMetadata.UploadStatus> statuses = new HashMap<>();
        MediaMetadata.UploadStatus status;
        for (String processName : processes.keySet()) {
            status = checkProcessStatus(processName);

            if (status == MediaMetadata.UploadStatus.ERROR) {
                System.out.println("\n==============================\n");
                System.out.println(getProcessError(processName));
            }

            statuses.put(processName, status);
        }
        return statuses;
    }

    public void hashChunks(String folderPath, int mediaId, Session session) throws IOException, NoSuchAlgorithmException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            ChunkHash toPersist;
            byte[] hash;
            for (File file : files) {
                if (file.isFile() && (file.getName().endsWith(".ts") || file.getName().endsWith(".m3u8"))) {
                    hash = hashFile(file);
                    toPersist = new ChunkHash(mediaId, file.getName(), hash);
                    session.persist(toPersist);
                }
            }
        }
    }

//    public void createFinishedFlag(String folderPath) throws RuntimeException, IOException, InterruptedException {
//        String[] commandList = new String[]{
//                "/bin/bash", "-c", LocalPaths.FLAG_SCRIPT
//        };
//        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
//        processBuilder.directory(new File(folderPath));
//        Process p = processBuilder.start();
//        int result = p.waitFor();
//        if (result != 0) {
//            throw new RuntimeException("Error creating finished flag");
//        }
//    }

    // -----------------------------------------------------------------
    // ------------------------ PRIVATE METHODS ------------------------
    // -----------------------------------------------------------------

    private byte[] hashFile(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        return digest.digest();
    }

    private void cleanProcesses() {
        for (String processName : processes.keySet()) {
            if (checkProcessStatus(processName) == MediaMetadata.UploadStatus.FINISHED) {
                processes.remove(processName);
            }
        }
    }

    private MediaMetadata.UploadStatus checkProcessStatus(String processName) {
        Process p = processes.get(processName);
        try {
            int exitValue = p.exitValue();
            if (exitValue == 0) {
                return MediaMetadata.UploadStatus.FINISHED;
            } else {
                return MediaMetadata.UploadStatus.ERROR;
            }
        } catch (IllegalThreadStateException e) {
            return MediaMetadata.UploadStatus.PROCESSING;
        }
    }

    private String getProcessError(String processName) throws IOException {
        Process p = processes.get(processName);

        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        ArrayList<String> list = new ArrayList<>();
        String line;
        while ((line = stdError.readLine()) != null) {
            list.add(line);
        }
        return list.stream().map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

}
