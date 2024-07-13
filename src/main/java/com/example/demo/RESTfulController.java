package com.example.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.*;

@RestController
public class RESTfulController {
    private final Database database = new Database();

    @CrossOrigin(origins = "http://localhost:3001")
    @PostMapping("/api/login")
    public String loginRequest(@RequestBody LoginRequest loginRequest){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        System.out.println("Log In Versuch: " + username);
        if (database.logIn(username, password)){
            return "Login erfolgreich für Benutzer: " + username;
        }
        return "Passwort/Username ist falsch";
    }

    @CrossOrigin(origins = "http://localhost:3001")
    @PostMapping("/api/register")
    public String register(@RequestBody LoginRequest loginRequest){
        boolean successful = database.addUser(loginRequest.getUsername(), loginRequest.getPassword());
        System.out.println("registry");
        if (successful){
            return "Registerung erfolgreich";
        }
        return "Username schon vergeben";
    }
}
