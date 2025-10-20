package com.kaiburr.taskapi.service;

import com.kaiburr.taskapi.model.Task;
import com.kaiburr.taskapi.model.TaskExecution;
import com.kaiburr.taskapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // Simple regex to allow only "echo" and alphanumeric arguments
    private static final Pattern SAFE_COMMAND_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s echo]+$");

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByName(String name) {
        return taskRepository.findByNameContainingIgnoreCase(name);
    }

    public Task saveOrUpdateTask(Task task) {
        // ** SECURITY VALIDATION **
        if (task.getCommand() == null || !SAFE_COMMAND_PATTERN.matcher(task.getCommand()).matches()) {
            throw new IllegalArgumentException("Command contains unsafe characters or is null.");
        }
        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public Optional<Task> executeTask(String taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            TaskExecution execution = new TaskExecution();
            execution.setStartTime(Instant.now());

            try {
                Process process = Runtime.getRuntime().exec(task.getCommand());
                StringBuilder output = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    execution.setOutput(output.toString());
                } else {
                    // Handle error stream if needed
                    execution.setOutput("Error executing command. Exit code: " + exitVal);
                }
            } catch (Exception e) {
                execution.setOutput("Failed to execute command: " + e.getMessage());
            }

            execution.setEndTime(Instant.now());
            task.getTaskExecutions().add(execution);
            taskRepository.save(task);
            return Optional.of(task);
        }
        return Optional.empty();
    }
}