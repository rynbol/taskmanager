package com.assignment.taskmanager;

import jakarta.persistence.*;
import lombok.Data;
import java.time.ZonedDateTime;

@Data // Lombok: creates getters, setters, etc. for you
@Entity // JPA: This is a database table
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean completed = false; // Default to false

    private java.time.ZonedDateTime createdAt;
    private java.time.ZonedDateTime updatedAt;

    @PrePersist // Runs before saving for the first time
    protected void onCreate() {
        this.createdAt = this.updatedAt = ZonedDateTime.now();
    }

    @PreUpdate // Runs before updating
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }
}