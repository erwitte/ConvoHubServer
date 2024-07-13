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

        if (database.logIn(username, password)){
            return "Login erfolgreich für Benutzer: " + username;
        }
        return "Passwort/Username ist falsch";
    }
}
