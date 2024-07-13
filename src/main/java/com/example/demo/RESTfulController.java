package com.example.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.*;

@RestController
public class RESTfulController {
    @CrossOrigin(origins = "http://localhost:3001")
    @PostMapping("/api/login")
    public String loginRequest(@RequestBody LoginRequest loginRequest){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        System.out.println(username + " " + password);


        //keine datenbank implementiert, vorerst nur mit admin admin
        if(username.equals("admin") && password.equals("admin")){
            System.out.println("est");
        }
        return "Login erfolgreich für Benutzer: " + username;
    }
}
