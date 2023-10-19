package main;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import tools.LocalPaths;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Main {

    public static final String BASE_URI = "http://localhost:8080/";

    private static void createUploadFolder() {
        // Deletes if it exists
        if (Files.exists(Path.of(LocalPaths.MEDIA_FOLDER))) {
            try {
                FileUtils.deleteDirectory(new File(LocalPaths.MEDIA_FOLDER));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Files.createDirectory(Path.of(LocalPaths.MEDIA_FOLDER));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Server startServer() throws Exception {

        createUploadFolder();

        // scan packages
        final ResourceConfig config = new ResourceConfig().packages("controller");

        //1.Creating the resource handler
        ResourceHandler resourceHandler= new ResourceHandler();
        //2.Setting Resource Base
        resourceHandler.setResourceBase(LocalPaths.MEDIA_FOLDER);
        //3.Enabling Directory Listing
        resourceHandler.setDirectoriesListed(true);
        //4.Setting Context Source
        ContextHandler contextHandler = new ContextHandler("/resources");
        //5.Attaching Handlers
        contextHandler.setHandler(resourceHandler);

        final Server server = JettyHttpContainerFactory.createServer(URI.create(BASE_URI), config, false);
        server.setHandler(contextHandler);
        server.start();

        return server;

    }

    public static void main(String[] args) throws Exception {

        try {

            final Server server = startServer();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("Shutting down the application...");
                    server.stop();
                    System.out.println("Done, exit.");
                } catch (Exception e) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
                }
            }));

            System.out.printf("Application started.%nStop the application using CTRL+C%n");

            // block and wait shut down signal, like CTRL+C
            Thread.currentThread().join();

            // alternative
            // Thread.sleep(Long.MAX_VALUE);       // sleep forever...
            // Thread.sleep(Integer.MAX_VALUE);    // sleep around 60+ years

        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}