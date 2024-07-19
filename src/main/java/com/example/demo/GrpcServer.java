package com.example.demo;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.CommandLineRunner;

import java.io.IOException;

public class GrpcServer implements CommandLineRunner {
    private Server grpcServer;
    private int port;
    private boolean isPortFound = false;

    public GrpcServer(int port) {
        this.port = port;
    }

    public int startServer() throws Exception {
        run();
        return port;
    }

    public void run(String... args) throws Exception {
        startGrpcServer();
    }

    private void startGrpcServer() throws IOException {
        while(!isPortFound) {
            // tries to set up a server on the current port until it works
            port = findFreePort(port);
        }
        System.out.println("gRPC server started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            if (grpcServer != null) {
                grpcServer.shutdown();
            }
        }));
    }

    private int findFreePort(int port) {
        try {
            grpcServer = ServerBuilder.forPort(port)
                    .addService(new MessageService())
                    .build()
                    .start();
            isPortFound = true;
            return port;
        } catch (IOException e) {
            return port + 1;
        }
    }
}
