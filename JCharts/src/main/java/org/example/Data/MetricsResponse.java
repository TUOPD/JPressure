package org.example.Data;

import lombok.Data;

@Data
public class MetricsResponse {

    private int qps;
    private int totalSuccess;
    private int totalFail;
    private double avgRt;
    private long tp90;
    private long tp99;
}