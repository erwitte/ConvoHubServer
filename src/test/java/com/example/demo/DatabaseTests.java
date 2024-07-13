package com.example.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class DatabaseTests {

    private static Database database ;

    @BeforeAll
    static void setUp() {
        database = new Database();
    }

    @AfterAll
    static void tearDown() {
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/convohub", "user", "password")) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_USERS");
            stmt.executeUpdate("DROP TABLE IF EXISTS USERS");
            stmt.executeUpdate("DROP TABLE IF EXISTS ROOMS");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testAddUserTrue() {
        assertTrue(database.addUser("user1", "password1"));
    }

    @Test
    void testAddUserDuplicate() {
        assertTrue(database.addUser("user2", "password2"));
        assertFalse(database.addUser("user2", "duplicate")); // Duplicate username
    }

    @Test
    void removeUserTrue() {
        assertTrue(database.removeUser("user2"));
    }

    //@Test
    //void removeUserFalse() {      }

    @Test
    void addRoomTrue() {
        assertTrue(database.addRoom("room1", "password1"));
    }

    @Test
    void removeRoomTrue() {
        assertTrue(database.removeRoom("room1"));
    }

    @Test
    void addRoomDuplicate() {
        assertTrue(database.addRoom("room2", "password3"));
        assertFalse(database.addRoom("room2", "duplicate"));
    }
}
