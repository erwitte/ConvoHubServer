package com.example.demo;
import java.sql.*;

import static java.lang.System.exit;

public class Database {
    private final String username, password, url;
    private Connection conn;

    public Database() {
        url = "jdbc:postgresql://localhost:5432/convohub";
        username = "user";
        password = "password";
        if (!setUpDatabase()){
            exit(0); // if there is no db the program exits
        }
        setUpTable();
    }

    private boolean setUpDatabase(){
        try{
            conn = DriverManager.getConnection(url, username, password);
            if (conn != null){
                System.out.println("connection to database established");
                return true;
            } else {
                System.out.println("connection to database failed");
                return false;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void setUpTable(){
        createUsersTable();
        createRoomsTable();
        createRoom_UsersTable();
        // catch blocks kick in if table already exists
    }

    private void createUsersTable(){
        String createUsersTable =   "CREATE TABLE USERS (" +
                "USERNAME VARCHAR(255) PRIMARY KEY," +
                "PASSWORD VARCHAR(255) NOT NULL);";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createUsersTable);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private void createRoomsTable(){
        String createRoomsTable =   "CREATE TABLE ROOMS (" +
                "ROOMNAME TEXT PRIMARY KEY," +
                "PASSWORD VARCHAR(255) NOT NULL);";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createRoomsTable);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private void createRoom_UsersTable(){
        String createRoom_UsersTable =  "CREATE TABLE ROOM_USERS (" +
                "ROOMNAME TEXT NOT NULL,"+
                "USERNAME TEXT NOT NULL," +
                "FOREIGN KEY (USERNAME) REFERENCES USERS(USERNAME)," +
                "FOREIGN KEY (ROOMNAME) REFERENCES ROOMS(ROOMNAME));";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createRoom_UsersTable);
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private boolean isExistsRoom(String roomName){
        String checkQuery = "SELECT * FROM ROOMS WHERE ROOMNAME = '" + roomName + "'";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(checkQuery);
            if (rs.next()){ // checks if there is an entry for that name
                return true;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean addRoom(String roomName, String password) {
        String addQuery = "INSERT INTO ROOMS (ROOMNAME, PASSWORD) VALUES ('"
                + roomName + "', '" + password + "')";
        if (isExistsRoom(roomName)) {
            return false;   // username already in database
        }

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(addQuery);
            return true;
        } catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean removeRoom(String roomName){
        String removeRoomrQuery = "DELETE FROM ROOMS WHERE ROOMNAME = '" + roomName + "'";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(removeRoomrQuery);
            return true;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean removeUser(String username){
        String removeUserQuery = "DELETE FROM USERS WHERE USERNAME = '" + username + "'";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(removeUserQuery);
            return true;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean addUser(String username, String password){
        String addQuery = "INSERT INTO USERS (username, password) VALUES ('"
                + username + "', '" + password + "')";
        if (isExistsUser(username)){
            return false;   // username already in database
        }

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(addQuery);
            return true;
        } catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    private boolean isExistsUser(String username){
        String checkQuery = "SELECT * FROM USERS WHERE USERNAME = '" + username + "'";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(checkQuery);
            if (rs.next()){ // checks if there is an entry for that name
                return true;
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }
}