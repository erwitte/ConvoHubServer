package com.example.demo;

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
    public void deleteUserFromRoom(@PathVariable("id") int id, HttpServletRequest request) {
        Cookie cookie = request.getCookies()[0];
        int userId = database.getUserId(cookie.getName());
        database.removeUserFromRoomById(userId, id);
    }
}
