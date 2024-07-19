package com.example.demo;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class RESTfulService implements      CommandLineRunner {
    private Server grpcServer;      

    public static void main(String[] args) {
        SpringApplication.run(RESTfulService.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        startGrpcServer();
    }

    private void startGrpcServer() throws IOException {
        grpcServer = ServerBuilder.forPort(9090)
                .addService(new MessageService())
                .build()
                .start();
        System.out.println("gRPC server started on port 9090");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            if (grpcServer != null) {
                grpcServer.shutdown();
            }
        }));
    }
}