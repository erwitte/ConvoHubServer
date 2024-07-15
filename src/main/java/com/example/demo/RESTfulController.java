package com.example.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class RESTfulController {
    private final Database database = new Database();
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/login")
    public ResponseEntity<String> loginRequest(@RequestBody LoginRequest loginRequest){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        System.out.println("Log In Versuch: " + username);

        //get password
        String hashPassword = database.getPasswordByUsername(username);
        System.out.println(hashPassword);

        //compare with bcrypt
        if(passwordEncoder.matches(password, hashPassword)){
            //create jwt token
            try{
                Algorithm algorithm = Algorithm.HMAC256("CWhdZS1t4N3Vul6ihk5/5mU5e0Z2St+8o4/pAWFZfA="); //das sollte man nicht machen (!)
                String token = JWT.create()
                        .withIssuer("ConvoHub Server")
                        .withSubject(username)
                        .sign(algorithm);

                System.out.println("true");
                return ResponseEntity.ok().body("{\"token\": \"" + token + "\"}");
            }catch (JWTCreationException exception){
                //....
            }

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }


    @PostMapping("/api/register")
    public ResponseEntity<Boolean> register(@RequestBody LoginRequest loginRequest){
        System.out.println("registry");

        //hash password with bcrypt serversided
        String hashedPassword = passwordEncoder.encode(loginRequest.getPassword());
        System.out.println(hashedPassword);
        if (database.addUser(loginRequest.getUsername(), hashedPassword)){
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    @GetMapping("/api/user")
    public String getUsername(@RequestHeader(value = "Authorization") String auth){
        DecodedJWT decodedJWT;
        String username="";
        try{
            Algorithm algorithm = Algorithm.HMAC256("CWhdZS1t4N3Vul6ihk5/5mU5e0Z2St+8o4/pAWFZfA=");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("ConvoHub Server")
                    .build();
            decodedJWT = verifier.verify(auth.substring(7));
            username = decodedJWT.getSubject();
        }catch (JWTVerificationException exception){
            //invalid
        }
        return "{\"username\": \"" + username + "\"}";
    }
}
