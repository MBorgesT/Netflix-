package com.example.client.mesh;

import android.util.Log;

import com.example.client.util.Resources;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class JettyServerManager {

    private static final String TAG = "JettyServerManager";

    private static Server server;

    public static void startServer() {
        server = new Server(9090); // Choose a port

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(Resources.getMediaDownloadFolder()); // Set your resource folder path

        server.setHandler(resourceHandler);

        try {
            server.start();
        } catch (Exception e) {
            Log.e(TAG, "server start error", e);
        }
    }

    public static void stopServer() {
        try {
            server.stop();
        } catch (Exception e) {
            Log.e(TAG, "server shutdown error", e);
        }
    }
}