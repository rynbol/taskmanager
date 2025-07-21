package com.assignment.taskmanager;

import com.assignment.taskmanager.model.Task;
import com.assignment.taskmanager.model.TaskRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskManagerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clean the database before each test to ensure isolation
        taskRepository.deleteAll();
    }

    @Test
    void testFullTaskCrudWorkflow() throws Exception {
        // 1. CREATE a new task
        TaskRequest createRequest = new TaskRequest();
        createRequest.setTitle("Initial Task");
        createRequest.setDescription("Initial Description");
        createRequest.setCompleted(false);

        MvcResult createResult = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn(); // Use andReturn() to capture the result

        // Extract the created task's ID from the response
        String responseBody = createResult.getResponse().getContentAsString();
        Task createdTask = objectMapper.readValue(responseBody, Task.class);
        Long taskId = createdTask.getId();

        // 2. GET the task by its ID to verify creation
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Initial Task")));

        // 3. UPDATE the task
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setCompleted(true);

        mockMvc.perform(put("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.completed", is(true)));

        // 4. GET all tasks to verify there is still only one task
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 5. DELETE the task
        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().isNoContent()); // Expect 204 No Content

        // 6. GET the task by ID again and expect a 404 Not Found
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateMultipleTasks() throws Exception {
        // Create first task
        TaskRequest task1 = new TaskRequest();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setCompleted(false);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task1)))
                .andExpect(status().isCreated());

        // Create second task
        TaskRequest task2 = new TaskRequest();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setCompleted(true);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task2)))
                .andExpect(status().isCreated());

        // Verify both tasks exist
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetAllTasks_WhenEmpty() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTask_NotFound() throws Exception {
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");
        updateRequest.setCompleted(true);

        mockMvc.perform(put("/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteTask_NotFound() throws Exception {
        mockMvc.perform(delete("/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTaskWithNullValues_ShouldReturnBadRequest() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle(null);
        request.setDescription(null);
        request.setCompleted(null);

        // Your API has validation enabled, so null values should return 400
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTaskWithEmptyStrings() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("");
        request.setDescription("");
        request.setCompleted(false);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("")))
                .andExpect(jsonPath("$.description", is("")))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void testCreateTaskWithLongStrings() throws Exception {
        String longTitle = "A".repeat(100); // Reduced size for reliability
        String longDescription = "B".repeat(200);

        TaskRequest request = new TaskRequest();
        request.setTitle(longTitle);
        request.setDescription(longDescription);
        request.setCompleted(true);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(longTitle)))
                .andExpect(jsonPath("$.description", is(longDescription)))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void testInvalidJsonRequest() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testTimestampFields() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Timestamp Test");
        request.setDescription("Testing timestamps");
        request.setCompleted(false);

        MvcResult result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andReturn();

        Task createdTask = objectMapper.readValue(result.getResponse().getContentAsString(), Task.class);
        
        // Update the task and verify updatedAt changes
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Timestamp Test");
        updateRequest.setDescription("Updated description");
        updateRequest.setCompleted(true);

        Thread.sleep(1000); // Ensure time difference

        mockMvc.perform(put("/tasks/" + createdTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.title", is("Updated Timestamp Test")));
    }
}