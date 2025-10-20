package com.kaiburr.taskapi.controller;

import com.kaiburr.taskapi.model.Task;
import com.kaiburr.taskapi.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // GET tasks (all, by ID, or by name)
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name) {
        if (id != null) {
            Optional<Task> task = taskService.getTaskById(id);
            return task.map(t -> ResponseEntity.ok(List.of(t)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else if (name != null) {
            List<Task> tasks = taskService.getTasksByName(name);
            if (tasks.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.ok(taskService.getAllTasks());
        }
    }

    // PUT a task (create or update)
    @PutMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            Task savedTask = taskService.saveOrUpdateTask(task);
            return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    // PUT to execute a task
    @PutMapping("/{taskId}/execute")
    public ResponseEntity<Task> executeTask(@PathVariable String taskId) {
        Optional<Task> updatedTask = taskService.executeTask(taskId);
        return updatedTask.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}