package utils;

import model.MediaMetadata;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public final class HLSPackager {

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

    public void packageVideo(String processName) throws IOException {
        //cleanProcesses();

        String mediaPath = LocalPaths.MEDIA_FOLDER + processName + "/";

        String[] commandList = new String[]{
                "/bin/bash", "-c", LocalPaths.PACKAGING_SCRIPT, " \n",
                "/bin/bash", "-c", LocalPaths.FLAG_SCRIPT
        };
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        processBuilder.directory(new File(mediaPath));
        Process p = processBuilder.start();

        processes.put(processName, p);
    }

    public boolean isVideoAlreadyBeingPackaged(String fileName) {
        //cleanProcesses();

        if (!processes.containsKey(fileName)) {
            return false;
        }
        return (checkProcessStatus(fileName) == MediaMetadata.UploadStatus.PROCESSING);
    }

    public HashMap<String, MediaMetadata.UploadStatus> getUploadStatuses() throws IOException {
        //cleanProcesses();

        HashMap<String, MediaMetadata.UploadStatus> statuses = new HashMap<>();
        MediaMetadata.UploadStatus status;
        for (String processName: processes.keySet()) {
            status = checkProcessStatus(processName);

            if (status == MediaMetadata.UploadStatus.ERROR) {
                //statusText += " - " + getProcessError(processName);
                System.out.println("\n==============================\n");
                System.out.println(getProcessError(processName));
            }

            statuses.put(processName, status);
        }
        return statuses;
    }

    // -----------------------------------------------------------------
    // ------------------------ PRIVATE METHODS ------------------------
    // -----------------------------------------------------------------

    private void cleanProcesses() {
        for (String processName: processes.keySet()) {
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

    private void logProcessOutput(String processName) throws IOException {
        Process p = processes.get(processName);

//        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        ArrayList<String> list = new ArrayList<>();
//        String line;
//        while ((line = stdError.readLine()) != null) {
//            list.add(line);
//        }
//        String output = list.stream().map(Object::toString)
//                .collect(Collectors.joining("\\\n"));

        String output = IOUtils.toString(p.getInputStream(), Charset.defaultCharset());
        System.out.println(output);
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

    private String getProcessOutput(String processName) throws IOException {
        Process p = processes.get(processName);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        ArrayList<String> list = new ArrayList<>();
        String line;
        while ((line = stdInput.readLine()) != null) {
            list.add(line);
        }
        return list.stream().map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

}
