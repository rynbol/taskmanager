package com.assignment.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TaskEntityTest {

    private TaskEntity taskEntity;

    @BeforeEach
    void setUp() {
        taskEntity = new TaskEntity();
    }

    @Test
    void testDefaultValues() {
        // Given - new TaskEntity
        TaskEntity newTask = new TaskEntity();

        // Then
        assertThat(newTask.isCompleted()).isFalse();
        assertThat(newTask.getId()).isNull();
        assertThat(newTask.getTitle()).isNull();
        assertThat(newTask.getDescription()).isNull();
        assertThat(newTask.getCreatedAt()).isNull();
        assertThat(newTask.getUpdatedAt()).isNull();
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Long id = 1L;
        String title = "Test Task";
        String description = "Test Description";
        boolean completed = true;
        ZonedDateTime now = ZonedDateTime.now();

        // When
        taskEntity.setId(id);
        taskEntity.setTitle(title);
        taskEntity.setDescription(description);
        taskEntity.setCompleted(completed);
        taskEntity.setCreatedAt(now);
        taskEntity.setUpdatedAt(now);

        // Then
        assertThat(taskEntity.getId()).isEqualTo(id);
        assertThat(taskEntity.getTitle()).isEqualTo(title);
        assertThat(taskEntity.getDescription()).isEqualTo(description);
        assertThat(taskEntity.isCompleted()).isEqualTo(completed);
        assertThat(taskEntity.getCreatedAt()).isEqualTo(now);
        assertThat(taskEntity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testOnCreate_SetsTimestamps() {
        // Given
        ZonedDateTime beforeCreate = ZonedDateTime.now().minusSeconds(1);

        // When
        taskEntity.onCreate();

        // Then
        ZonedDateTime afterCreate = ZonedDateTime.now().plusSeconds(1);
        assertThat(taskEntity.getCreatedAt()).isNotNull();
        assertThat(taskEntity.getUpdatedAt()).isNotNull();
        assertThat(taskEntity.getCreatedAt()).isAfter(beforeCreate);
        assertThat(taskEntity.getCreatedAt()).isBefore(afterCreate);
        assertThat(taskEntity.getUpdatedAt()).isEqualTo(taskEntity.getCreatedAt());
    }

    @Test
    void testOnUpdate_UpdatesOnlyUpdatedAt() {
        // Given
        ZonedDateTime originalTime = ZonedDateTime.now().minusHours(1);
        taskEntity.setCreatedAt(originalTime);
        taskEntity.setUpdatedAt(originalTime);

        ZonedDateTime beforeUpdate = ZonedDateTime.now().minusSeconds(1);

        // When
        taskEntity.onUpdate();

        // Then
        ZonedDateTime afterUpdate = ZonedDateTime.now().plusSeconds(1);
        assertThat(taskEntity.getCreatedAt()).isEqualTo(originalTime); // Should not change
        assertThat(taskEntity.getUpdatedAt()).isNotNull();
        assertThat(taskEntity.getUpdatedAt()).isAfter(beforeUpdate);
        assertThat(taskEntity.getUpdatedAt()).isBefore(afterUpdate);
        assertThat(taskEntity.getUpdatedAt()).isAfter(taskEntity.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setTitle("Task 1");

        TaskEntity task2 = new TaskEntity();
        task2.setId(1L);
        task2.setTitle("Task 1");

        TaskEntity task3 = new TaskEntity();
        task3.setId(2L);
        task3.setTitle("Task 2");

        // Then - Lombok @Data should provide equals and hashCode
        assertThat(task1).isEqualTo(task2);
        assertThat(task1).isNotEqualTo(task3);
        assertThat(task1.hashCode()).isEqualTo(task2.hashCode());
        assertThat(task1.hashCode()).isNotEqualTo(task3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        taskEntity.setId(1L);
        taskEntity.setTitle("Test Task");
        taskEntity.setDescription("Test Description");
        taskEntity.setCompleted(true);

        // When
        String toString = taskEntity.toString();

        // Then - Lombok @Data should provide toString
        assertThat(toString).contains("TaskEntity");
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("title=Test Task");
        assertThat(toString).contains("description=Test Description");
        assertThat(toString).contains("completed=true");
    }
}
