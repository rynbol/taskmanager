package com.assignment.taskmanager;

import com.assignment.taskmanager.model.TaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TasksApiController.class)
class TasksApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskEntity sampleTaskEntity;
    private TaskRequest sampleTaskRequest;

    @BeforeEach
    void setUp() {
        sampleTaskEntity = new TaskEntity();
        sampleTaskEntity.setId(1L);
        sampleTaskEntity.setTitle("Sample Task");
        sampleTaskEntity.setDescription("Sample Description");
        sampleTaskEntity.setCompleted(false);
        sampleTaskEntity.setCreatedAt(ZonedDateTime.now());
        sampleTaskEntity.setUpdatedAt(ZonedDateTime.now());

        sampleTaskRequest = new TaskRequest();
        sampleTaskRequest.setTitle("Sample Task");
        sampleTaskRequest.setDescription("Sample Description");
        sampleTaskRequest.setCompleted(false);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        // Given
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(sampleTaskEntity);

        // When & Then
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleTaskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Sample Task")))
                .andExpect(jsonPath("$.description", is("Sample Description")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(taskRepository, times(1)).save(any(TaskEntity.class));
    }

    @Test
    void createTask_WithEmptyData_ShouldReturnBadRequest() throws Exception {
        // Given - empty task request with null required fields
        TaskRequest emptyRequest = new TaskRequest();
        // title and description will be null, which should trigger validation
        
        // When & Then - Your API has validation enabled, so null values should return 400
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());

        // Repository should not be called due to validation failure
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        // Given
        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setCompleted(true);
        task2.setCreatedAt(ZonedDateTime.now());
        task2.setUpdatedAt(ZonedDateTime.now());

        List<TaskEntity> tasks = Arrays.asList(sampleTaskEntity, task2);
        when(taskRepository.findAll()).thenReturn(tasks);

        // When & Then
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Sample Task")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Task 2")))
                .andExpect(jsonPath("$[1].completed", is(true)));

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getAllTasks_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(taskRepository.findAll()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_WhenExists_ShouldReturnTask() throws Exception {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTaskEntity));

        // When & Then
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Sample Task")))
                .andExpect(jsonPath("$.description", is("Sample Description")))
                .andExpect(jsonPath("$.completed", is(false)));

        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    void updateTask_WhenExists_ShouldReturnUpdatedTask() throws Exception {
        // Given
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setCompleted(true);

        TaskEntity updatedEntity = new TaskEntity();
        updatedEntity.setId(1L);
        updatedEntity.setTitle("Updated Task");
        updatedEntity.setDescription("Updated Description");
        updatedEntity.setCompleted(true);
        updatedEntity.setCreatedAt(sampleTaskEntity.getCreatedAt());
        updatedEntity.setUpdatedAt(ZonedDateTime.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTaskEntity));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(updatedEntity);

        // When & Then
        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(TaskEntity.class));
    }

    @Test
    void updateTask_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setCompleted(true);

        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).save(any(TaskEntity.class));
    }

    @Test
    void deleteTask_WhenExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTaskEntity));

        // When & Then
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(sampleTaskEntity);
    }

    @Test
    void deleteTask_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskRepository, times(1)).findById(999L);
        verify(taskRepository, never()).delete(any(TaskEntity.class));
    }
}
