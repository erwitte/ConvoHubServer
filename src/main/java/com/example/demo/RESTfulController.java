package com.example.demo;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
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

import static com.example.demo.ServerCrypto.printDebug;

@RestController
public class RESTfulController {
    private final Database database = Database.getInstance();
    private final String cryptoSecret = "CWhdZS1t4N3Vul6ihk5/5mU5e0Z2St+8o4/pAWFZfA=";
    @Autowired
    private PasswordEncoder passwordEncoder;

     @PostMapping("/api/login")
    public ResponseEntity<String> loginRequest(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        printDebug("login", username+" "+password);

        //get password
        String hashPassword = database.getPasswordByUsername(username);

        //compare with bcrypt
        if(passwordEncoder.matches(password, hashPassword)){
            try{
                String token = ServerCrypto.createTokenForLoginUser(username);
                response.addCookie(ServerCrypto.createSessionCookie(token)); // Füge das Cookie zur Antwort hinzu
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
    public ResponseEntity<Boolean> logout(HttpServletResponse response, HttpServletRequest request){
         printDebug("logout", request.getRemoteUser());
       if(request.getCookies() != null){
           response.addCookie(ServerCrypto.createLogoutCookie());
           printDebug("logout", "done");
           return ResponseEntity.status(HttpStatus.OK).body(true);
       }
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    @GetMapping("/api/user")
    public ResponseEntity<String> getUsername(@CookieValue("jwtToken") String token){
        printDebug("user", token);
        String username="";
        try{
            if(ServerCrypto.checkIfUserIsLegit(token)){
                username = ServerCrypto.getUsernameFromToken(token);
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
        String username="";
        printDebug("channel", token);
        try{
            if(ServerCrypto.checkIfUserIsLegit(token)){
                username = ServerCrypto.getUsernameFromToken(token);
                ResultSet rs = database.getUserRoomsResultSet(database.getUserId(username));
                List<Room> rooms = database.convertResultSetRoomToList(rs);
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

    @DeleteMapping("/api/delete")
    public ResponseEntity<Boolean> deleteUser(@CookieValue("jwtToken") String token){
         int userId = database.getUserId(ServerCrypto.getUsernameFromToken(token));
         if (database.removeUserById(userId)){
             return ResponseEntity.status(HttpStatus.OK).body(true);
         }
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }


}
