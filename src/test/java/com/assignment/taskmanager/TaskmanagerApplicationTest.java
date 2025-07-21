package com.assignment.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskmanagerApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TasksApiController tasksApiController;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void contextLoads() {
        // Verify that the Spring application context loads successfully
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void controllersAreCreated() {
        // Verify that our main components are created and injected
        assertThat(tasksApiController).isNotNull();
        assertThat(taskRepository).isNotNull();
    }

    @Test
    void applicationStartsSuccessfully() {
        // This test will fail if the application cannot start
        // due to configuration issues, missing beans, etc.
        assertThat(applicationContext.getBeanDefinitionCount()).isGreaterThan(0);
    }
}
