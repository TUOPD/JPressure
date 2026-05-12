package org.example.SampleResult;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SampleResult {


    private long startTime;
    private long endTime;
    private long responseTime;

    private boolean success = true;
    private String errorMessage;
    private Throwable exception;


    private int statusCode;


    private Object responseData;


    private long responseSize;


    private int affectedRows;

    private String threadName;
    private long threadId;

    private Map<String, Object> attributes = new HashMap<>();

    public void sampleStart() {
        this.startTime = System.nanoTime();
        this.threadName = Thread.currentThread().getName();
        this.threadId = Thread.currentThread().getId();
    }

    public void sampleEnd() {
        this.endTime = System.nanoTime();
        this.responseTime = (endTime - startTime) / 1_000_000; // ms
    }


    public void addAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}
