package com.kaiburr.taskapi.model;

import lombok.Data;
import java.time.Instant;

@Data // Lombok annotation for getters, setters, toString, etc.
public class TaskExecution {
    private Instant startTime;
    private Instant endTime;
    private String output;
}