package com.kaiburr.taskapi.controller;

import com.kaiburr.taskapi.model.Task;
import com.kaiburr.taskapi.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Task>> getTasksByName(@PathVariable String name) {
        List<Task> tasks = taskService.getTasksByName(name);
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @PutMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        if (task.getId() == null || task.getId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Task ID cannot be null or empty.");
        }
        try {
            Task savedTask = taskService.saveOrUpdateTask(task);
            return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // THIS IS THE CORRECTED METHOD
    @PutMapping("/{id}/execute")
    public ResponseEntity<Task> executeTask(@PathVariable String id) {
        Optional<Task> updatedTaskOptional = taskService.executeTask(id);
        return updatedTaskOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}