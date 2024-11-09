package org.habitApp.controllers;

import org.habitApp.domain.dto.userDto.UserDtoLogin;
import org.habitApp.domain.dto.userDto.UserDtoRegisterUpdate;
import org.habitApp.domain.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class AuditTestControllerTest {

    @Mock
    private MockMvc mockMvc;

    @InjectMocks
    private AuditTestController auditTestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditTestController).build();
    }

    @Test
    @DisplayName("GET /audit-test/fast - Быстрый метод")
    void shouldReturnFastMethodResponse() throws Exception {
        mockMvc.perform(get("/audit-test/fast"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("This is a fast method"));
    }

    @Test
    @DisplayName("GET /audit-test/slow - Медленный метод")
    void shouldReturnSlowMethodResponse() throws Exception {
        mockMvc.perform(get("/audit-test/slow"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("This is a slow method"));
    }
}
