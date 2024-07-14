package com.example.demo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.when;
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
        database = new Database();
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPass");
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
    public void testLoginRequestTrue() throws Exception {   
        database.addUser("testUser", "testPass");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testLoginRequestFalse() throws Exception {
        var loginRequestFalse = new LoginRequest();
        loginRequestFalse.setUsername("testUser");
        loginRequestFalse.setPassword("testPassFalse");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestFalse)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("false"));
    }


}
