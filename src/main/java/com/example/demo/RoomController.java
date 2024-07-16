package com.example.demo;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final Database database = Database.getInstance();

    @PutMapping("/create")
    public void createRoom(@RequestBody CreateRoomRequest room) {
        if (database.addRoom(room.roomName(), "passwort")){
            System.out.println("Room " + room.roomName() + " created");
            //database.addUserToRoom()
        }
    }
}
