package ru.ylab.levon.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
class HelloControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void hello_whenCalled_returnsJson() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void apiIndex_whenCalled_returnsJsonMap() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$.service").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.auth").isArray())
                .andExpect(jsonPath("$.products_user").isArray())
                .andExpect(jsonPath("$.products_admin").isArray())
                .andExpect(jsonPath("$.audit").isArray());
    }
}
