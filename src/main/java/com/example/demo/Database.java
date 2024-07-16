package com.example.demo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class Database {
    private final String username, password, url;
    private static Connection conn;
    private static boolean isCreated = false;

    private static Database database;

    Database() {
        url = "jdbc:postgresql://localhost:5432/convohub";
        username = "user";
        password = "password";
        if (!setUpDatabase()){
            exit(0); // if there is no db the program exits
        }
        setUpTable();
        isCreated = true;
    }
    // Singleton pattern to prevent problems by initializing db multiple times
    public static Database getInstance() {
        if (!isCreated){
            database = new Database();
        }
        return database;
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
                "ID SERIAL PRIMARY KEY, " +
                "USERNAME VARCHAR(255) NOT NULL, " +
                "PASSWORD VARCHAR(255) NOT NULL);";
        executeQuery(createUsersTable);
    }

    private void createRoomsTable(){
        String createRoomsTable =   "CREATE TABLE ROOMS (" +
                "ID SERIAL PRIMARY KEY, " +
                "ROOMNAME VARCHAR(255) NOT NULL);";
        executeQuery(createRoomsTable);

        addRoom("Welcome Room");
    }

    private void createRoom_UsersTable(){
        String createRoom_UsersTable =  "CREATE TABLE ROOM_USERS (" +
                "USER_ID INTEGER NOT NULL, " +
                "ROOM_ID INTEGER NOT NULL, " +
                "PRIMARY KEY (USER_ID, ROOM_ID)," +
                "FOREIGN KEY (USER_ID) REFERENCES USERS(ID) ON DELETE CASCADE ," +
                "FOREIGN KEY (ROOM_ID) REFERENCES ROOMS(ID) ON DELETE CASCADE );";
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

    public String getPasswordByUsername(String username){
        String passwordQuery = "SELECT PASSWORD FROM USERS WHERE USERNAME = '" + username + "'";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(passwordQuery);

            if(rs.next()){
                return rs.getString("password");
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean addRoom(String roomName) {
        String addQuery = "INSERT INTO ROOMS (ROOMNAME) VALUES ('"
                + roomName + "')";
        if (isExistsRoom(roomName)) {
            return false;   // username already in database
        }
        return executeQuery(addQuery);
    }

    public boolean removeRoomByName(String roomName){
        String removeRoomrQuery = "DELETE FROM ROOMS WHERE ROOMNAME = '" + roomName + "'";
        return executeQuery(removeRoomrQuery);
    }

    public boolean removeRoomById(int id){
        String removeRoomrQuery = "DELETE FROM ROOMS WHERE ID = '" + id + "'";
        return executeQuery(removeRoomrQuery);
    }

    public int getRoomId(String roomName){
        String getRoomIdQuery = "SELECT ID FROM ROOMS WHERE ROOMNAME = '" + roomName + "'";
        ResultSet rs = getResultSet(getRoomIdQuery);
        try {
            return rs.getInt("ID");
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public boolean removeUser(String username){
        String removeUserQuery = "DELETE FROM USERS WHERE USERNAME = '" + username + "'";
        return executeQuery(removeUserQuery);
    }

    public boolean removeUserById(int id){
        String removeUserQuery = "DELETE FROM USERS WHERE ID = " + id;
        return executeQuery(removeUserQuery);
    }

    public boolean addUser(String username, String password){
        String addQuery = "INSERT INTO USERS (username, password) VALUES ('"
                + username + "', '" + password + "')";
        if (isExistsUser(username)){
            return false;   // username already in database
        }
        return executeQuery(addQuery);
    }


    public boolean logIn(String username, String password){
        String logInQuery = "SELECT * FROM USERS WHERE USERNAME = '" + username + "'";
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(logInQuery);
            if (rs.next()){
                String passwordInDb = rs.getString("password");
                return password.equals(passwordInDb);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
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

    private boolean executeQuery(String query){
        try{
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean removeUserFromRoom(String username, String roomName){
        try{
            ResultSet userResultSet = getUserResultSetName(username);
            ResultSet roomResultSet = getRoomsResultSet(roomName);
            int userId = userResultSet.getInt("ID");
            int roomId = roomResultSet.getInt("ID");
            String removeUserFromRoomQuery = "DELETE FROM ROOM_USERS WHERE USER_ID = " + userId + " AND ROOM_ID = " + roomId;
            return executeQuery(removeUserFromRoomQuery);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean removeUserFromRoomById(int userId, int roomId){
        String removeUserFromRoomQuery = "DELETE FROM ROOM_USERS WHERE USER_ID = " + userId + " AND ROOM_ID = " + roomId;
        return executeQuery(removeUserFromRoomQuery);
    }

    public boolean addUserToRoom(String username, String roomName){
        try {
            ResultSet userResultSet = getUserResultSetName(username);
            ResultSet roomResultSet = getRoomsResultSet(roomName);
            int userId = userResultSet.getInt("ID");
            int roomId = roomResultSet.getInt("ID");
            String addUserToRoomQuery = "INSERT INTO ROOM_USERS (USER_ID, ROOM_ID) VALUES (" + userId + ", " +
                    roomId + ")";
            return executeQuery(addUserToRoomQuery);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    private ResultSet getUserResultSetName(String username){
        String getUserQuery = "SELECT * FROM USERS WHERE USERNAME = '" + username + "'";
        return getResultSet(getUserQuery);
    }

    private ResultSet getRoomsResultSet(String roomName){
        String getRoomsQuery = "SELECT * FROM ROOMS WHERE ROOMNAME = '" + roomName + "'";
        return getResultSet(getRoomsQuery);
    }

    public int getUserId(String username){
        String getUserIdQuery = "SELECT * FROM USERS WHERE USERNAME = '" + username + "'";
        ResultSet rs = getResultSet(getUserIdQuery);
        try {

            return rs.getInt("ID");
        } catch (Exception e){
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public List<Room> convertResultSetToList(ResultSet resultSet) throws SQLException {
        List<Room> rooms = new ArrayList<>();

        try {
            Room room = new Room();
            room.setRoomId(resultSet.getInt("id"));
            room.setRoomName(resultSet.getString("roomname"));
            rooms.add(room);
            while (resultSet.next()) {
                room = new Room();
                room.setRoomId(resultSet.getInt("id"));
                room.setRoomName(resultSet.getString("roomname"));
                rooms.add(room);
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return rooms;
    }

    public ResultSet getUserRoomsResultSet(int userId) {
        // Query, um die RoomIDs zu bekommen, denen der Nutzer zugeordnet ist
        String getUserRoomsQuery = "SELECT room_id FROM room_users WHERE user_id = " + userId;
        System.out.println(getUserRoomsQuery);
        // ResultSet für die RoomIDs
        ResultSet roomIdsResultSet = getResultSet(getUserRoomsQuery);
        System.out.println(roomIdsResultSet);

        StringBuilder roomIdsString = new StringBuilder();
        try{
            while(roomIdsResultSet.next()){
                roomIdsString.append(roomIdsResultSet.getInt("room_id"));
                roomIdsString.append(", ");
            }
            if (roomIdsString.length() > 0) {
                roomIdsString.setLength(roomIdsString.length() - 2);
            }

        }catch (Exception e){

        }
        System.out.println("formatted strbuild: ("+ roomIdsString.toString() + ")");
        String getRoomNameQuery = "SELECT id, roomname FROM rooms WHERE id IN ("+roomIdsString+")";
        System.out.println(getRoomNameQuery);
        //ResultSet für Rooms
        ResultSet roomCompleteRs = getResultSet(getRoomNameQuery);

        return roomCompleteRs;
    }

    private ResultSet getResultSet(String query){
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            return rs;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}