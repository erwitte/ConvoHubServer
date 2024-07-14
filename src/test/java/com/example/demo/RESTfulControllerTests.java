package com.example.demo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RESTfulController.class)
public class RESTfulControllerTests {

    @Autowired
    private MockMvc mockMvc; // Injects the MockMvc instance
    private static Database database;
    @Autowired
    private ObjectMapper objectMapper;

    private static LoginRequest loginRequest;

    @BeforeAll
    static void setUp() {
        loginRequest = new LoginRequest();
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
    @Order(1)
    public void testLoginRequestTrue() throws Exception {
        database.addUser("testUser", "testPass");
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPass");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @Order(2)
    public void testLoginRequestFalse() throws Exception {
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassFalse");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("false"));
    }

    @Test
    public void testRegistrationRequestTrue() throws Exception {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testRegistration");
        loginRequest.setPassword("testPass");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testRegistrationRequestFalse() throws Exception {
        // register user
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testRegistration1");
        loginRequest.setPassword("testPass");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)));
        // try same username for registration again
        loginRequest.setUsername("testRegistration1");
        loginRequest.setPassword("testPassFalse");
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("false"));
    }
}
