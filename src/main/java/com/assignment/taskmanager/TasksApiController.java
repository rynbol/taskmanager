package com.assignment.taskmanager;

import com.assignment.taskmanager.api.TasksApi;
import com.assignment.taskmanager.model.Task; // Generated API Model
import com.assignment.taskmanager.model.TaskRequest; // Generated API Model
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TasksApiController implements TasksApi {

    @Autowired
    private TaskRepository taskRepository;

    /* MAPPING LOGIC */

    private Task convertToDto(TaskEntity entity) {
        Task dto = new Task();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCompleted(entity.isCompleted());
        // The model uses OffsetDateTime, so we need to convert
        if (entity.getCreatedAt() != null) {
            dto.setCreatedAt(entity.getCreatedAt().toOffsetDateTime());
        }
        if (entity.getUpdatedAt() != null) {
            dto.setUpdatedAt(entity.getUpdatedAt().toOffsetDateTime());
        }
        return dto;
    }

    private TaskEntity convertToEntity(TaskRequest dto) {
        TaskEntity entity = new TaskEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setCompleted(dto.getCompleted());
        return entity;
    }

    /* API IMPLEMENTATIONS */

    // POST request to create a new task
    @Override
    public ResponseEntity<Task> createTask(TaskRequest taskRequest) {
        TaskEntity entityToSave = convertToEntity(taskRequest);
        TaskEntity savedEntity = taskRepository.save(entityToSave);
        return new ResponseEntity<>(convertToDto(savedEntity), HttpStatus.CREATED);
    }
    // DELETE a task by ID
    @Override
    public ResponseEntity<Void> deleteTask(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    // GET all tasks
    @Override
    public ResponseEntity<List<Task>> getAllTasks() {
        List<TaskEntity> entities = taskRepository.findAll();
        List<Task> dtos = entities.stream()
                                  .map(this::convertToDto)
                                  .collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
    // Get task by ID
    @Override
    public ResponseEntity<Task> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(entity -> new ResponseEntity<>(convertToDto(entity), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    // PUT request to update a task by ID
    @Override
    public ResponseEntity<Task> updateTask(Long id, TaskRequest taskRequest) {
        return taskRepository.findById(id)
                .map(existingEntity -> {
                    existingEntity.setTitle(taskRequest.getTitle());
                    existingEntity.setDescription(taskRequest.getDescription());
                    existingEntity.setCompleted(taskRequest.getCompleted());
                    TaskEntity updatedEntity = taskRepository.save(existingEntity);
                    return new ResponseEntity<>(convertToDto(updatedEntity), HttpStatus.OK);
                }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}