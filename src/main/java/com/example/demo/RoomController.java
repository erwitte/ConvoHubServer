package com.example.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final Database database = Database.getInstance();

    @PutMapping("/create")
    public void createRoom(@RequestBody CreateRoomRequest room, HttpServletRequest request) {
        Cookie cookie = request.getCookies()[0];
        if (database.addRoom(room.roomName())){
            System.out.println("Room " + room.roomName() + " created");
            database.addUserToRoom(cookie.getName(), room.roomName());
        }
    }

    @DeleteMapping("/deleteRoom/{id}")
    public void deleteRoom(@PathVariable("id") int id) {
        if (database.removeRoomById(id)){
            System.out.println("Room " + id + " deleted");
        }
    }

    @DeleteMapping("/deleteUserFromRoom/{id}")
    public void deleteUserFromRoom(@PathVariable("id") int id, @CookieValue("jwtToken") String token) {
        int userId = database.getUserId(getUsernameFromToken(token));
        database.removeUserFromRoomById(userId, id);    
    }

    private String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT;
        Algorithm algorithm = Algorithm.HMAC256("CWhdZS1t4N3Vul6ihk5/5mU5e0Z2St+8o4/pAWFZfA=");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("ConvoHub Server")
                .build();
        decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }
}
