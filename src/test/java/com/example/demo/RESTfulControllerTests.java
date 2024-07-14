package com.example.demo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RESTfulController.class)
public class RESTfulControllerTests {

    @Autowired
    private MockMvc mockMvc; // Injects the MockMvc instance
    @MockBean
    private static Database database;
    @Autowired
    private ObjectMapper objectMapper;

    private static LoginRequest loginRequest;

    @BeforeAll
    public static void setup() {
        //database = new Database();
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPass");
    }

    @Test
    public void testLoginRequest_Success() throws Exception {
        // run twice, remove mock annotaion on database to really add user to db other tested method doesnt work
        database.addUser("testUser", "testPass");
        when(database.logIn("testUser", "testPass")).thenReturn(true);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
