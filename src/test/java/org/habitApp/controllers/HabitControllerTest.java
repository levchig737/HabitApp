package org.habitApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.habitApp.domain.dto.habitDto.HabitDtoCreateUpdate;
import org.habitApp.domain.dto.habitDto.HabitDtoResponse;
import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.HabitEntity;
import org.habitApp.models.Period;
import org.habitApp.services.HabitService;
import org.habitApp.mappers.HabitMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HabitService habitService;

    @Mock
    private HabitMapper habitMapper;

    @InjectMocks
    private HabitController habitController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private HabitEntity testHabit;
    private HabitDtoCreateUpdate habitDtoCreateUpdate;
    private HabitDtoResponse habitDtoResponse;
    private HabitReportDto reportDto;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HabitController(habitService, habitMapper)).build();
        testHabit = new HabitEntity(1L, "Test Habit", "Description", Period.DAY.toString(), LocalDate.now(), 1L);
        habitDtoCreateUpdate = new HabitDtoCreateUpdate("Test Habit", "Description", Period.DAY.toString());
        habitDtoResponse = new HabitDtoResponse(1L, "Test Habit", "Description", Period.DAY.toString(), 1L);
        reportDto = new HabitReportDto(1L, 3, 5, 5, Period.DAY);

    }

    @Test
    @DisplayName("POST /habits - Создать привычку")
    void shouldCreateHabit() throws Exception {

        mockMvc.perform(post("/habits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitDtoCreateUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("Habit created successfully."));
    }

    @Test
    @DisplayName("PUT /habits/{id} - Обновить привычку")
    void shouldUpdateHabit() throws Exception {

        mockMvc.perform(put("/habits/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitDtoCreateUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("Habit updated successfully."));
    }

    @Test
    @DisplayName("DELETE /habits/{id} - Удалить привычку")
    void shouldDeleteHabit() throws Exception {
        doNothing().when(habitService).deleteHabit(anyLong());

        mockMvc.perform(delete("/habits/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Habit deleted successfully."));
    }

    @Test
    @DisplayName("GET /habits - Получить все привычки")
    void shouldGetAllHabits() throws Exception {
        List<HabitEntity> filteredHabits = Collections.singletonList(testHabit);

        when(habitService.getAllHabits()).thenReturn(filteredHabits);
        when(habitMapper.habitToHabitDtoResponse(any())).thenReturn(habitDtoResponse);

        mockMvc.perform(get("/habits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Habit"))
                .andExpect(jsonPath("$[0].description").value("Description"));
    }

    @Test
    @DisplayName("GET /habits/{id} - Получить привычку по ID")
    void shouldGetHabitById() throws Exception {
        when(habitService.getHabitById(anyLong())).thenReturn(testHabit);
        when(habitMapper.habitToHabitDtoResponse(any())).thenReturn(habitDtoResponse);

        mockMvc.perform(get("/habits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Habit"))
                .andExpect(jsonPath("$.description").value("Description"));
    }


    @Test
    @DisplayName("POST /habits/{id}/complete - Отметить привычку выполненной")
    void shouldMarkHabitCompleted() throws Exception {
        doNothing().when(habitService).markHabitAsCompleted(anyLong());

        mockMvc.perform(post("/habits/1/complete"))
                .andExpect(status().isOk())
                .andExpect(content().string("Habit marked as completed."));
    }

    @Test
    @DisplayName("GET /habits/{id}/report/{period} - Получение отчета о привычке")
    void shouldGetHabitReport() throws Exception {
        when(habitService.calculateHabitCompletedByPeriod(any(), any())).thenReturn(5);
        when(habitService.calculateCurrentStreak(any())).thenReturn(3);

        mockMvc.perform(get("/habits/1/report/WEEK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completionCount").value(5))
                .andExpect(jsonPath("$.streak").value(3));
    }

    @Test
    @DisplayName("GET /habits/{id}/completion-percentage/{period} - Получение процента выполнения привычки")
    void shouldGetCompletionPercentage() throws Exception {
        when(habitService.calculateCompletionPercentage(any(), any())).thenReturn(75.0);

        mockMvc.perform(get("/habits/1/completion-percentage/WEEK"))
                .andExpect(status().isOk())
                .andExpect(content().string("75.0"));
    }

    @Test
    @DisplayName("GET /habits/{id}/progress-report/{period} - Генерация отчета о прогрессе привычки")
    void shouldGenerateProgressReport() throws Exception {
        when(habitService.generateProgressReport(any(), any())).thenReturn(reportDto);

        mockMvc.perform(get("/habits/1/progress-report/MONTH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completionCount").value(5))
                .andExpect(jsonPath("$.streak").value(3));
    }
}
