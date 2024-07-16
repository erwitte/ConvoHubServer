package com.example.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RestController
public class RESTfulController {
    private final Database database = Database.getInstance();
    @Autowired
    private PasswordEncoder passwordEncoder;

   /* @PostMapping("/api/login")
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
    }*/

     @PostMapping("/api/login")
    public ResponseEntity<String> loginRequest(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
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
                Cookie cookie = new Cookie("jwtToken", token);
                cookie.setHttpOnly(true); // Cookie ist nur über HTTP erreichbar
                cookie.setSecure(false); // Erfordert HTTPS für das Cookie
                cookie.setPath("/"); // Setze den Pfad des Cookies
                cookie.setMaxAge(2000);
                cookie.setAttribute("SameSite", "None");
                cookie.setDomain("localhost");
                response.addCookie(cookie); // Füge das Cookie zur Antwort hinzu
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
            database.addUserToRoom(loginRequest.getUsername(), "Welcome Room");
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    @PostMapping("/api/logout")
    public ResponseEntity<Boolean> register(HttpServletResponse response, HttpServletRequest request){
       System.out.println(">> RESTful IN: /api/logout FOR "+request.getPathInfo());
       System.out.println(">> RESTful OUT: overwrite cookie to expire FOR "+request.getPathInfo());
       if(request.getCookies() != null){
           Cookie cookie = new Cookie("jwtToken", "");
           cookie.setMaxAge(0);
           cookie.setHttpOnly(true); // Cookie ist nur über HTTP erreichbar
           cookie.setSecure(false); // Erfordert HTTPS für das Cookie
           cookie.setPath("/"); // Setze den Pfad des Cookie;
           cookie.setAttribute("SameSite", "None");
           cookie.setDomain("localhost");
           response.addCookie(cookie);
           System.out.println(">> RESTful OUT: done FROM "+request.getPathInfo());
           return ResponseEntity.status(HttpStatus.OK).body(true);
       }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }


    @GetMapping("/api/user")
    public ResponseEntity<String> getUsername(@CookieValue("jwtToken") String token){
        DecodedJWT decodedJWT;
        String username="";
        System.out.println(token);
        try{
            Algorithm algorithm = Algorithm.HMAC256("CWhdZS1t4N3Vul6ihk5/5mU5e0Z2St+8o4/pAWFZfA=");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("ConvoHub Server")
                    .build();
            decodedJWT = verifier.verify(token);
            if(database.getUserId(decodedJWT.getSubject())  != -1){
                username = decodedJWT.getSubject();
                return ResponseEntity.status(HttpStatus.OK).body("{\"username\": \"" + username + "\"}");
            } else {
                throw new JWTVerificationException("User database error");
            }
        }catch (JWTVerificationException exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }
    }


    //die token abfrage und die zusätzliche datenbankabfrage für den nutzer muss in eine eigene funktion, damit das nicht immer aufgerufen werden muss
    @GetMapping("/api/channel")
    public ResponseEntity<String> getChannels(@CookieValue("jwtToken") String token){
        DecodedJWT decodedJWT;
        String username="";
        try{
            Algorithm algorithm = Algorithm.HMAC256("CWhdZS1t4N3Vul6ihk5/5mU5e0Z2St+8o4/pAWFZfA=");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("ConvoHub Server")
                    .build();
            decodedJWT = verifier.verify(token);
            if(database.getUserId(decodedJWT.getSubject())  != -1){
                username = decodedJWT.getSubject();
                ResultSet rs = database.getUserRoomsResultSet(database.getUserId(username));
                List<Room> rooms = database.convertResultSetToList(rs);
                //json convert
                ObjectMapper objectMapper = new ObjectMapper();
                String json = objectMapper.writeValueAsString(rooms); //to json
                return ResponseEntity.ok(json);

            } else {
                throw new JWTVerificationException("User database error");
            }
        }catch (JWTVerificationException exception){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    String getTokenStrFromCookies(Cookie[] cookies){
        String authToken = "";
        System.out.println("komme rein");
        if(cookies != null){
            for(Cookie cookie : cookies){
                System.out.println(cookie.getName());
                if(cookie.getName().equals("jwtToken")){
                    authToken = cookie.getName();
                    System.out.println("AUTH TOKEN: "+authToken);
                }
            }
        }

        return authToken;
    }
}
