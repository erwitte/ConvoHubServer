package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
//@SpringBootApplication
public class RESTfulService {

    /*public static void main(String[] args) {
        //SpringApplication.run(RESTfulService.class, args);
    }*/

        public static void main(String[] args) throws Exception {
            Server server = ServerBuilder.forPort(8080)
                    .addService(new MessageService())
                    .build()
                    .start();

            System.out.println("Server started, listening on " + 8080);
            server.awaitTermination();
        }
    }
