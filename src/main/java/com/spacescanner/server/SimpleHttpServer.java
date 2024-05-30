package com.spacescanner.server;

import io.helidon.common.http.Http;
import io.helidon.webclient.WebClient;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.WebServer;
import io.helidon.common.http.MediaType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class SimpleHttpServer {

    public void startServer() throws Exception {
        WebServer server = WebServer.builder()
                .routing(createRouting())
                .port(8080)
                .build();

        CompletionStage<WebServer> startFuture = server.start();
        startFuture.thenAccept(ws -> System.out.println("Server started at: http://localhost:" + ws.port() + "/"))
                .exceptionally(t -> {
                    System.err.println("Startup failed: " + t.getMessage());
                    return null;
                });

        // Wait for the server to start (this blocks until the server is started or an exception occurs)
        startFuture.toCompletableFuture().get(10, TimeUnit.SECONDS); // Wait for 10 seconds
    }

    private Routing createRouting() {
        return Routing.builder()
                .get("/", (req, res) -> res.send("Hello from Helidon HTTP Server!"))
                .get("/api/external-data", (req, res) -> handleLaunchesApi(req, res)) // Define API endpoint with lambda expression for handler
                .get("/api/json-data", this::serveJsonFile) // Define route to serve JSON file
                .build();
    }

    private void handleLaunchesApi(ServerRequest req, ServerResponse res) {
        WebClient.create()
                .get()
                .uri("https://api.spacexdata.com/v4/launches")
                .request()
                .thenAccept(response -> response.content()
                        .as(String.class)
                        .thenAccept(body -> res.send(body)))
                .exceptionally(ex -> {
                    res.status(Http.Status.INTERNAL_SERVER_ERROR_500);
                    res.send(ex.getMessage());
                    return null;
                });
    }

    private void serveJsonFile(ServerRequest req, ServerResponse res) {
        try {
            // Specify the path to your JSON file
            Path jsonFilePath = Paths.get("/Users/aaronsmith/development/code/personal-projects/space-scanner/output.json");

            // Read the file content
            String content = new String(Files.readAllBytes(jsonFilePath));

            // Send the JSON file content as the response
            res.headers().contentType(MediaType.APPLICATION_JSON);
            res.send(content);
        } catch (IOException e) {
            // Handle any errors, such as file not found
            res.status(500).send("Error serving JSON file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            SimpleHttpServer httpServer = new SimpleHttpServer();
            httpServer.startServer();
        } catch (Exception e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
