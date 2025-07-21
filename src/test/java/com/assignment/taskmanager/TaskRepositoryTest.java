package com.assignment.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private TaskEntity sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new TaskEntity();
        sampleTask.setTitle("Sample Task");
        sampleTask.setDescription("Sample Description");
        sampleTask.setCompleted(false);
        sampleTask.setCreatedAt(ZonedDateTime.now());
        sampleTask.setUpdatedAt(ZonedDateTime.now());
    }

    @Test
    void testSaveAndFindById() {
        // When
        TaskEntity savedTask = taskRepository.save(sampleTask);
        Optional<TaskEntity> foundTask = taskRepository.findById(savedTask.getId());

        // Then
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Sample Task");
        assertThat(foundTask.get().getDescription()).isEqualTo("Sample Description");
        assertThat(foundTask.get().isCompleted()).isFalse();
        assertThat(foundTask.get().getCreatedAt()).isNotNull();
        assertThat(foundTask.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindById_WhenNotExists() {
        // When
        Optional<TaskEntity> foundTask = taskRepository.findById(999L);

        // Then
        assertThat(foundTask).isEmpty();
    }

    @Test
    void testFindAll() {
        // Given
        TaskEntity task1 = new TaskEntity();
        task1.setTitle("Task 1");
        task1.setDescription("Description 1");
        task1.setCompleted(false);
        task1.setCreatedAt(ZonedDateTime.now());
        task1.setUpdatedAt(ZonedDateTime.now());

        TaskEntity task2 = new TaskEntity();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setCompleted(true);
        task2.setCreatedAt(ZonedDateTime.now());
        task2.setUpdatedAt(ZonedDateTime.now());

        taskRepository.save(task1);
        taskRepository.save(task2);

        // When
        List<TaskEntity> allTasks = taskRepository.findAll();

        // Then
        assertThat(allTasks).hasSize(2);
        assertThat(allTasks).extracting(TaskEntity::getTitle)
                .containsExactlyInAnyOrder("Task 1", "Task 2");
    }

    @Test
    void testFindAll_WhenEmpty() {
        // When
        List<TaskEntity> allTasks = taskRepository.findAll();

        // Then
        assertThat(allTasks).isEmpty();
    }

    @Test
    void testUpdate() {
        // Given
        TaskEntity savedTask = taskRepository.save(sampleTask);
        Long taskId = savedTask.getId();

        // When
        savedTask.setTitle("Updated Title");
        savedTask.setDescription("Updated Description");
        savedTask.setCompleted(true);
        TaskEntity updatedTask = taskRepository.save(savedTask);

        // Then
        Optional<TaskEntity> foundTask = taskRepository.findById(taskId);
        assertThat(foundTask).isPresent();
        assertThat(foundTask.get().getTitle()).isEqualTo("Updated Title");
        assertThat(foundTask.get().getDescription()).isEqualTo("Updated Description");
        assertThat(foundTask.get().isCompleted()).isTrue();
        assertThat(foundTask.get().getId()).isEqualTo(taskId);
    }

    @Test
    void testDelete() {
        // Given
        TaskEntity savedTask = taskRepository.save(sampleTask);
        Long taskId = savedTask.getId();

        // When
        taskRepository.delete(savedTask);

        // Then
        Optional<TaskEntity> foundTask = taskRepository.findById(taskId);
        assertThat(foundTask).isEmpty();
    }

    @Test
    void testDeleteById() {
        // Given
        TaskEntity savedTask = taskRepository.save(sampleTask);
        Long taskId = savedTask.getId();

        // When
        taskRepository.deleteById(taskId);

        // Then
        Optional<TaskEntity> foundTask = taskRepository.findById(taskId);
        assertThat(foundTask).isEmpty();
    }

    @Test
    void testCount() {
        // Given
        taskRepository.save(sampleTask);
        
        TaskEntity task2 = new TaskEntity();
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setCompleted(true);
        task2.setCreatedAt(ZonedDateTime.now());
        task2.setUpdatedAt(ZonedDateTime.now());
        taskRepository.save(task2);

        // When
        long count = taskRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testExistsById() {
        // Given
        TaskEntity savedTask = taskRepository.save(sampleTask);
        Long taskId = savedTask.getId();

        // When & Then
        assertThat(taskRepository.existsById(taskId)).isTrue();
        assertThat(taskRepository.existsById(999L)).isFalse();
    }

    @Test
    void testJpaLifecycleCallbacks() {
        // Given
        TaskEntity newTask = new TaskEntity();
        newTask.setTitle("Lifecycle Test");
        newTask.setDescription("Testing JPA callbacks");
        newTask.setCompleted(false);

        // When - save triggers @PrePersist
        TaskEntity savedTask = taskRepository.save(newTask);

        // Then
        assertThat(savedTask.getCreatedAt()).isNotNull();
        assertThat(savedTask.getUpdatedAt()).isNotNull();
        assertThat(savedTask.getCreatedAt()).isEqualTo(savedTask.getUpdatedAt());

        // When - update triggers @PreUpdate
        ZonedDateTime originalCreatedAt = savedTask.getCreatedAt();
        // Add small delay to ensure different timestamps
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        savedTask.setTitle("Updated Title");
        TaskEntity updatedTask = taskRepository.save(savedTask);

        // Then
        assertThat(updatedTask.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(updatedTask.getUpdatedAt()).isAfterOrEqualTo(originalCreatedAt);
    }
}
