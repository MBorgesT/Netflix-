package com.example.client.util;

import android.os.Build;
import android.util.Log;

import com.example.client.api.MediaAPI;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaDownloadUtil {

    public interface OnMediaDownloadListener {
        void onMediaDownload();

        void onMediaDownloadFailed();
    }

    private static final String TAG = "MediaDownloadUtil";

    public static void initMediaDownloadFolder() {
        String folderPath = Resources.getAppContext().getFilesDir() + "/MediaDownload";
        createFolderIfDoesntExist(folderPath);
    }

    public static void downloadFiles(List<String> uris, String folderName,
                                     OnMediaDownloadListener listener) {
        MediaAPI api = ApiBuilder.create(MediaAPI.class);

        String folderPath = Resources.getAppContext().getFilesDir() + "/MediaDownload/" + folderName;
        createFolderIfDoesntExist(folderPath);

        CountDownLatch latch = new CountDownLatch(uris.size());
        final int[] failureCount = {0};

        for (String fileName : uris) {
            String url = Resources.backendResourcesUrl + folderName + "/" + fileName;
            String filePath = folderPath + "/" + fileName;

            api.downloadMedia(url).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    latch.countDown();
                    if (response.isSuccessful()) {
                        boolean success = writeResponseBodyToDisk(response.body(), filePath);
                        if (!success) {
                            failureCount[0]++;
                        }
                    } else {
                        failureCount[0]++;
                    }

                    if (latch.getCount() == 0) {
                        // All requests are completed, check if there were any failures
                        if (failureCount[0] == 0) {
                            listener.onMediaDownload();
                        } else {
                            listener.onMediaDownloadFailed();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "request failed", t);
                    latch.countDown(); // Decrease the count on each failure
                    failureCount[0]++; // Increment failure count on failure

                    if (latch.getCount() == 0) {
                        // All requests are completed, check if there were any failures
                        if (failureCount[0] == 0) {
                            listener.onMediaDownload();
                        } else {
                            listener.onMediaDownloadFailed();
                        }
                    }
                }
            });
//            downloadFile(
//                    Resources.backendResourcesUrl + folderName + "/" + fileName,
//                    folderPath + "/" + fileName,
//                    api, listener
//            );
        }
    }

    public static void deleteMediaDownloadFolder(String folderName) {
        String folderPath = Resources.getAppContext().getFilesDir() + "/MediaDownload/" + folderName;
        File toDelete = new File(folderPath);
        deleteRecursive(toDelete);
    }

    private static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    private static void createFolderIfDoesntExist(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    private static boolean writeResponseBodyToDisk(ResponseBody body, String filePath) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = new BufferedInputStream(body.byteStream());
                outputStream = new FileOutputStream(filePath);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
