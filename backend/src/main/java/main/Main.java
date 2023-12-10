package main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.proxy.ProxyServlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import utils.HibernateUtil;
import utils.LocalPaths;

public class Main {

    public static final String BASE_URI = "http://localhost:8080/";

    private static void createUploadFolder() {

        if (!Files.exists(Path.of(LocalPaths.MEDIA_FOLDER))) {
            try {
                Files.createDirectory(Path.of(LocalPaths.MEDIA_FOLDER));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static Server startServer() throws Exception {

        createUploadFolder();

//        // scan packages
//        final ResourceConfig config = new ResourceConfig().packages("controller");
//
//        final Server server = JettyHttpContainerFactory.createServer(URI.create(BASE_URI), config, false);
//        server.start();

        Server server = new Server(LocalPaths.SERVER_PORT);

        // Create a ServletContextHandler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        server.setHandler(context);

        // Add your Jersey application as a servlet
        ServletHolder apiServlet = context.addServlet(ServletContainer.class, "/api/*");
        apiServlet.setInitOrder(0);
        apiServlet.setInitParameter("jersey.config.server.provider.packages", "controller");

        // Add a ProxyServlet for other resources (e.g., proxying to another service)
        ServletHolder proxyServlet = context.addServlet(ProxyServlet.Transparent.class, "/resources/*");
        proxyServlet.setInitParameter("proxyTo", LocalPaths.NGINX_ADDRESS); // Change the proxyTo URL as needed

        server.start();
        return server;
    }

    public static Server startServer2() throws Exception {
        createUploadFolder();

        // Create a ResourceConfig instance and register packages or classes
        ResourceConfig config = new ResourceConfig();
        config.packages("controller");
        config.register(MultiPartFeature.class);

        // Create a ServletContainer with the ResourceConfig
        ServletContainer servletContainer = new ServletContainer(config);

        // Create a ServletContextHandler
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Add your Jersey application as a servlet
        ServletHolder apiServlet = new ServletHolder(servletContainer);
        context.addServlet(apiServlet, "/api/*");
        apiServlet.setInitOrder(0);

        // Add a ProxyServlet for other resources (e.g., proxying to another service)
        ServletHolder proxyServlet = context.addServlet(ProxyServlet.Transparent.class, "/resources/*");
        proxyServlet.setInitParameter("proxyTo", LocalPaths.NGINX_ADDRESS); // Change the proxyTo URL as needed

        // Create and start the server
        Server server = new Server(LocalPaths.SERVER_PORT);
        server.setHandler(context);
        server.start();

        return server;
    }

    public static void main(String[] args) throws Exception {

        try {

            HibernateUtil.init();

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