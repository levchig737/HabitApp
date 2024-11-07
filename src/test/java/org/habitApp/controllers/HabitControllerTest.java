package org.habitApp.controllers;

import org.habitApp.domain.dto.habitDto.HabitDtoCreateUpdate;
import org.habitApp.domain.dto.habitDto.HabitDtoResponse;
import org.habitApp.exceptions.HabitNotFoundException;
import org.habitApp.exceptions.UnauthorizedAccessException;
import org.habitApp.mappers.HabitMapper;
import org.habitApp.services.impl.HabitServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HabitControllerTest {

    @Mock
    private HabitServiceImpl habitService;
    @Mock
    private HabitMapper habitMapper;
    @Mock
    private CurrentUserBean currentUserBean;
    @InjectMocks
    private HabitController habitController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ** Тесты для createHabit **
    @Test
    void testCreateHabit_Success() throws SQLException {
        HabitDtoCreateUpdate habitDto = new HabitDtoCreateUpdate("New Habit", "Description", "week");
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doNothing().when(habitService).createHabit(any(), anyString(), anyString(), any());

        ResponseEntity<?> response = habitController.createHabit(habitDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Habit created successfully.", response.getBody());
    }

    @Test
    void testCreateHabit_Unauthorized() {
        HabitDtoCreateUpdate habitDto = new HabitDtoCreateUpdate("New Habit", "Description", "week");
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = habitController.createHabit(habitDto);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void testCreateHabit_SQLException() throws SQLException {
        HabitDtoCreateUpdate habitDto = new HabitDtoCreateUpdate("New Habit", "Description", "week");
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doThrow(new SQLException("DB error")).when(habitService).createHabit(any(), anyString(), anyString(), any());

        ResponseEntity<?> response = habitController.createHabit(habitDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("DB error", response.getBody());
    }

    // ** Тесты для updateHabit **
    @Test
    void testUpdateHabit_Success() throws SQLException, HabitNotFoundException {
        HabitDtoCreateUpdate habitDto = new HabitDtoCreateUpdate("Updated Habit", "Description", "day");
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doNothing().when(habitService).updateHabit(anyLong(), anyString(), anyString(), any());

        ResponseEntity<?> response = habitController.updateHabit(1L, habitDto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Habit updated successfully.", response.getBody());
    }

    @Test
    void testUpdateHabit_NotFoundException() throws SQLException, HabitNotFoundException {
        HabitDtoCreateUpdate habitDto = new HabitDtoCreateUpdate("Updated Habit", "Description", "day");
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doThrow(new HabitNotFoundException("Habit not found")).when(habitService).updateHabit(anyLong(), anyString(), anyString(), any());

        ResponseEntity<?> response = habitController.updateHabit(1L, habitDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Habit not found", response.getBody());
    }

    @Test
    void testUpdateHabit_Unauthorized() {
        HabitDtoCreateUpdate habitDto = new HabitDtoCreateUpdate("Updated Habit", "Description", "day");
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = habitController.updateHabit(1L, habitDto);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }

    // ** Тесты для deleteHabit **
    @Test
    void testDeleteHabit_Success() throws SQLException, UnauthorizedAccessException, HabitNotFoundException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doNothing().when(habitService).deleteHabit(anyLong());

        ResponseEntity<?> response = habitController.deleteHabit(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Habit deleted successfully.", response.getBody());
    }

    @Test
    void testDeleteHabit_NotFoundException() throws SQLException, UnauthorizedAccessException, HabitNotFoundException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doThrow(new HabitNotFoundException("Habit not found")).when(habitService).deleteHabit(anyLong());

        ResponseEntity<?> response = habitController.deleteHabit(1L);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Habit not found", response.getBody());
    }

    @Test
    void testDeleteHabit_UnauthorizedAccessException() throws SQLException, UnauthorizedAccessException, HabitNotFoundException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doThrow(new UnauthorizedAccessException("Unauthorized access")).when(habitService).deleteHabit(anyLong());

        ResponseEntity<?> response = habitController.deleteHabit(1L);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Unauthorized access", response.getBody());
    }

    // ** Тесты для getAllHabits **

    @Test
    void testGetAllHabits_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = habitController.getAllHabits();

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void testGetAllHabits_SQLException() throws SQLException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        when(habitService.getAllHabits(any())).thenThrow(new SQLException("DB error"));

        ResponseEntity<?> response = habitController.getAllHabits();

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("DB error", response.getBody());
    }

    // ** Тесты для getHabitById **
    @Test
    void testGetHabitById_Success() throws SQLException, HabitNotFoundException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        HabitDtoResponse habitDtoResponse = new HabitDtoResponse("Habit 1", "Description 1", "DAILY");
        when(habitMapper.habitToHabitDtoResponse(any())).thenReturn(habitDtoResponse);

        ResponseEntity<?> response = habitController.getHabitById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(habitDtoResponse, response.getBody());
    }

    @Test
    void testGetHabitById_NotFoundException() throws SQLException, HabitNotFoundException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        when(habitService.getHabitById(anyLong(), any())).thenThrow(new HabitNotFoundException("Habit not found"));

        ResponseEntity<?> response = habitController.getHabitById(1L);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Habit not found", response.getBody());
    }

    @Test
    void testGetHabitById_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = habitController.getHabitById(1L);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }

    // ** Тесты для markHabitCompleted **
    @Test
    void testMarkHabitCompleted_Success() throws SQLException, HabitNotFoundException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doNothing().when(habitService).markHabitAsCompleted(anyLong());

        ResponseEntity<?> response = habitController.markHabitCompleted(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Habit marked as completed.", response.getBody());
    }

    @Test
    void testMarkHabitCompleted_NotFoundException() throws SQLException, HabitNotFoundException {
        when(currentUserBean.isAuthenticated()).thenReturn(true);
        doThrow(new HabitNotFoundException("Habit not found")).when(habitService).markHabitAsCompleted(anyLong());

        ResponseEntity<?> response = habitController.markHabitCompleted(1L);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Habit not found", response.getBody());
    }

    @Test
    void testMarkHabitCompleted_Unauthorized() {
        when(currentUserBean.isAuthenticated()).thenReturn(false);

        ResponseEntity<?> response = habitController.markHabitCompleted(1L);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }
}
