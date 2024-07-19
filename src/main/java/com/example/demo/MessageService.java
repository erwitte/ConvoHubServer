package com.example.demo;

import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class MessageService extends MessageGrpc.MessageImplBase {

    private final List<StreamObserver<ChatMessage>> observers = new ArrayList<>();

    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
        observers.add(responseObserver);
        System.out.println("count: " + observers.size())        ;

        return new StreamObserver<ChatMessage>() {
            @Override
            public void onNext(ChatMessage chatMessage) {
                // Handle the incoming message
                System.out.println("Received message from user: " + chatMessage.getUser() + ", message: " + chatMessage.getMessage());

                // Broadcast the received message to all observers
                for (StreamObserver<ChatMessage> observer : observers) {
                    observer.onNext(chatMessage);
                }
            }

            @Override
            public void onError(Throwable t) {
                observers.remove(responseObserver);
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                observers.remove(responseObserver);
                responseObserver.onCompleted();
            }
        };
    }
}

