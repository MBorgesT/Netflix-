package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public final class HLSPackager {

    enum ProcessStatus {
        RUNNING,
        FINISHED,
        ERROR,
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

    public void packageVideo(String processName) throws IOException {
        //cleanProcesses();

        String mediaPath = LocalPaths.MEDIA_FOLDER + processName + "/";

        String[] commandList = new String[]{"/bin/bash", "-c", LocalPaths.PACKAGING_SCRIPT};
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
        return (checkProcessStatus(fileName) == ProcessStatus.RUNNING);
    }

    public HashMap<String, String> getUploadStatuses() throws IOException {
        //cleanProcesses();

        HashMap<String, String> statuses = new HashMap<>();
        ProcessStatus status;
        String statusText;
        for (String processName: processes.keySet()) {
            status = checkProcessStatus(processName);
            statusText = status.name();
            if (status == ProcessStatus.ERROR) {
                //statusText += " - " + getProcessError(processName);
                System.out.println(getProcessError(processName));
            }

            statuses.put(processName, statusText);
        }
        return statuses;
    }

    // -----------------------------------------------------------------
    // ------------------------ PRIVATE METHODS ------------------------
    // -----------------------------------------------------------------

    private void cleanProcesses() {
        for (String processName: processes.keySet()) {
            if (checkProcessStatus(processName) == ProcessStatus.FINISHED) {
                processes.remove(processName);
            }
        }
    }

    private ProcessStatus checkProcessStatus(String processName) {
        Process p = processes.get(processName);
        try {
            int exitValue = p.exitValue();
            if (exitValue == 0) {
                return ProcessStatus.FINISHED;
            } else {
                return ProcessStatus.ERROR;
            }
        } catch (IllegalThreadStateException e) {
            return ProcessStatus.RUNNING;
        }
    }

    private void logProcessOutput(String processName) throws IOException {
        Process p = processes.get(processName);

        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        ArrayList<String> list = new ArrayList<>();
        String line;
        while ((line = stdError.readLine()) != null) {
            list.add(line);
        }
        String output = list.stream().map(Object::toString)
                .collect(Collectors.joining("\\\n"));
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

}
