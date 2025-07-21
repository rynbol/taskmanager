package com.assignment.taskmanager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    // This interface gives you all the database methods for free:
    // save(), findById(), findAll(), deleteById(), etc.
}