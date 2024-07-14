package com.example.demo;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3001")
@RestController
public class RESTfulController {
    private final Database database = new Database();


    @PostMapping("/api/login")
    public ResponseEntity<Boolean> loginRequest(@RequestBody LoginRequest loginRequest){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        System.out.println("Log In Versuch: " + username);
        if(database.logIn(username, password)){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    @PostMapping("/api/register")
    public ResponseEntity<Boolean> register(@RequestBody LoginRequest loginRequest){
        System.out.println("registry");
        if (database.addUser(loginRequest.getUsername(), loginRequest.getPassword())){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }
}
