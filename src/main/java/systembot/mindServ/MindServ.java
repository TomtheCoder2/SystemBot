package systembot.mindServ;

import arc.util.Log;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import systembot.mindServ.req.HandleGet;
import systembot.mindServ.req.HandleMap;
import systembot.mindServ.req.HandleSchem;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class MindServ {
    public static final String assets = "content/";
    public static final int port = 6969;
    private static final String AlphaNumericString =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "0123456789" +
                    "abcdefghijklmnopqrstuvxyz";
    public static ContentHandler contentHandler = new ContentHandler();
    public static HttpServer server;
    public static ThreadPoolExecutor executor;

    @Autowired
    public MindServ() {
        main(new String[0]);
    }

    public static void main(String[] args) {
        Log.info("Loading server.");
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            Log.err(e);
            System.exit(1);
        }
        File contentDir = new File(assets);
        if (!contentDir.exists() && !contentDir.mkdir()) {
            Log.err("Could not create content directory.");
            System.exit(0);
        }
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        server.createContext("/", new HandleGet());
        server.createContext("/map", new HandleMap());
        server.createContext("/schematic", new HandleSchem());
        server.setExecutor(executor);
        server.start();

        Log.info("Server started on port " + port + ".");
    }

    public static void emptyResponse(HttpExchange ex, int code) {
        try {
            ex.sendResponseHeaders(code, -1);
        } catch (IOException e) {
            Log.err(e);
        }
    }

    public static void fileResponse(HttpExchange ex, File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            OutputStream body = ex.getResponseBody();
            ex.sendResponseHeaders(200, bytes.length);
            body.write(bytes);
            body.close();
        } catch (Exception e) {
            emptyResponse(ex, 500);
            Log.err(e);
        }
    }

    public static String randomString(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(AlphaNumericString.charAt((int) (Math.random() * AlphaNumericString.length())));
        }
        return sb.toString();
    }
}
