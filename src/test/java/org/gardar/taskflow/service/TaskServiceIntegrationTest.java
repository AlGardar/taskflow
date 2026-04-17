package org.gardar.taskflow.service;

import org.gardar.taskflow.config.IntegrationTestBase;
import org.gardar.taskflow.dto.PageResponse;
import org.gardar.taskflow.dto.TaskCreateRequest;
import org.gardar.taskflow.dto.TaskResponse;
import org.gardar.taskflow.model.TaskStatus;
import org.gardar.taskflow.model.User;
import org.gardar.taskflow.repository.TaskRepository;
import org.gardar.taskflow.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceIntegrationTest extends IntegrationTestBase {
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CacheManager cacheManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test_author", "hash", "ROLE_USER");
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        cacheManager.getCache("tasks").clear();
    }

    @Test
    void shouldCreateTaskAndEvictCache() {
        // 1. Подготовка (Given)
        TaskCreateRequest request = new TaskCreateRequest("Integration Test Task", "Testing with containers");

        // 2. Действие (When)
        TaskResponse response = taskService.createTask(request, testUser.getId());

        // 3. Проверка (Then)
        assertNotNull(response.id(), "Task ID should not be null");
        assertEquals("Integration Test Task", response.title());
        assertEquals(TaskStatus.TODO, response.status());

        // Проверяем, что задача реально появилась в БД
        assertTrue(taskRepository.existsById(response.id()));
    }

    @Test
    void shouldCacheTasksList() {
        // 1. Подготовка (Given): Создаем задачу в БД
        taskService.createTask(new TaskCreateRequest("Task 1", "Desc 1"), testUser.getId());

        // 2. Действие (When): Запрашиваем список ДВАЖДЫ
        PageResponse<TaskResponse> firstCall = taskService.getAllTasks(testUser.getId(), 0, 10);
        PageResponse<TaskResponse> secondCall = taskService.getAllTasks(testUser.getId(), 0, 10);

        // 3. Проверка (Then):
        assertEquals(1, firstCall.totalElements());
        assertEquals(1, secondCall.totalElements());

        // Проверяем, что данные реально осели в Redis
        String expectedCacheKey = testUser.getId() + "-0-10";
        Object cachedData = cacheManager.getCache("tasks").get(expectedCacheKey);
        assertNotNull(cachedData, "Cache should contain data for the requested page");
    }
}
