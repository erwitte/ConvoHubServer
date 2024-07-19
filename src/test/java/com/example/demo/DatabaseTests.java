package com.example.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.*;

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
            tearDownMessageTable(conn);
            stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_USERS");
            stmt.executeUpdate("DROP TABLE IF EXISTS USERS");
            stmt.executeUpdate("DROP TABLE IF EXISTS ROOMS");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void tearDownMessageTable(Connection conn){
        String getMessageTables = "SELECT ID FROM ROOMS;";
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(getMessageTables);
            while(rs.next()){
                int id = rs.getInt("ID");
                String droptMessageTable = "DROP TABLE IF EXISTS ROOM_MESSAGES_" + id;
                stmt.executeUpdate(droptMessageTable);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    void testAddUserTrue() {
        assertTrue(database.addUser("user1", "password1"));
    }

    @Test
    void testAddUserDuplicate() {
        assertTrue(database.addUser("user5", "password2"));
        assertFalse(database.addUser("user5", "duplicate")); // Duplicate username
    }

    @Test
    void removeUserTrue() {
        database.addUser("userRemove", "password1");
        assertTrue(database.removeUser("userRemove"));
    }

    //@Test
    //void removeUserFalse() {      }

    @Test
    void addRoomTrue() {
        assertTrue(database.addRoom("addRoomTrue"));
    }

    @Test
    void removeRoomByNameTrue() {
        assertTrue(database.addRoom("roomRemove"));
        assertTrue(database.removeRoomByName("roomRemove"));
    }

    @Test
    void removeRoomByIdFalse() {
        database.addRoom("roomRemoveById");
        int id = database.getRoomId("roomRemoveById");
        assertTrue(database.removeRoomById(id));
    }

    @Test
    void addRoomDuplicate() {
        assertTrue(database.addRoom("roomDuplicate"));
        assertFalse(database.addRoom("roomDuplicate"));
    }

    @Test
    void logInTrueTest(){
        database.addUser("logIn", "password");
        assertTrue(database.logIn("logIn", "password"));
    }

    @Test
    void logInFalseTest(){
        assertFalse(database.logIn("false", "password"));
    }

    @Test
    void add1UserToRoomTrue(){
        database.addUser("addUserToRoom", "password");
        database.addRoom("addRoomToUser");
        assertTrue(database.addUserToRoom("addUserToRoom", "addRoomToUser"));
    }

    @Test
    void add2UsersToRoomTrue(){
        database.addUser("addUserToRoom1", "password");
        database.addUser("addUserToRoom2", "password2");
        database.addRoom("addRoomToUser2");
        assertTrue(database.addUserToRoom("addUserToRoom1", "addRoomToUser2"));
        assertTrue(database.addUserToRoom("addUserToRoom2", "addRoomToUser2"));
    }
    
    @Test
    void addUserToRoomFalse(){
        assertFalse(database.addUserToRoom("addUserToRoom2", "addRoomToUser"));
    }

    @Test
    void removeUserFromRoomTrue(){
        database.addUser("test", "password");
        database.addRoom("est");
        database.addUserToRoom("test", "est");
        assertTrue(database.removeUserFromRoom("test", "est"));
    }

    @Test
    void removeNonExistingUserFromRoom(){
        assertFalse(database.removeUserFromRoom("false", "est"));
    }

    @Test
    void removeUserFromNonExistingRoom(){
        assertFalse(database.removeUserFromRoom("true", "false"));
    }

    @Test
    void getUserIdTest(){
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/convohub", "user", "password")) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS ROOM_USERS");
            stmt.executeUpdate("DROP TABLE IF EXISTS USERS");
            stmt.executeUpdate("DROP TABLE IF EXISTS ROOMS");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        database = new Database()       ;
        database.addUser("user1", "password1");
        database.addUser("user2", "password2");
        assertSame(1, database.getUserId("user1"));
        assertSame(2, database.getUserId("user2"));
    }

    @Test
    void createTableForRoomMessagesTest(){
        assertTrue(database.createTableForRoomMessages(1));
        assertTrue(database.addMessageToMessageTable("est", 1));
        database.dropTableForRoomMessages(1);
        // to pass comment out creation of "welcome room" in createRoomsTable
    }
}
