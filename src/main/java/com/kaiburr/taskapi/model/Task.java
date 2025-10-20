package com.kaiburr.taskapi.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "tasks") // Maps this class to the "tasks" collection in MongoDB
public class Task {
    @Id // Marks this field as the primary key
    private String id;

    private String name;
    private String owner;
    private String command;
    private List<TaskExecution> taskExecutions = new ArrayList<>();
}