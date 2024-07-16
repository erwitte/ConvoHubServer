package com.example.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
public class RoomController {
    private final Database database = Database.getInstance();

    @PutMapping("/create")
    public ResponseEntity<Boolean> createRoom(@RequestBody CreateRoomRequest room, @CookieValue("jwtToken") String token) {
        System.out.println("room created");
        if (database.addRoom(room.createChannelName())){
            System.out.println("Room " + room.createChannelName() + " created");
            database.addUserToRoom(ServerCrypto.getUsernameFromToken(token), room.createChannelName());
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(true);
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
