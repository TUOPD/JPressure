package org.example.Listen;

import org.example.SampleResult.SampleResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsListener implements Listener {

    //总统计
    private final AtomicInteger totalSuccess = new AtomicInteger();
    private final AtomicInteger totalFail = new AtomicInteger();

    // 每秒统计
    private final AtomicInteger secondSuccess = new AtomicInteger();
    private final AtomicInteger secondFail = new AtomicInteger();

    // 响应时间集合
    private final List<Long> responseTimes =
            new CopyOnWriteArrayList<>();

    @Override
    public void onSampleResult(SampleResult result) {

        if (result.isSuccess()) {
            totalSuccess.incrementAndGet();
            secondSuccess.incrementAndGet();
        } else {
            totalFail.incrementAndGet();
            secondFail.incrementAndGet();
        }

        responseTimes.add(result.getResponseTime());
    }

    // 每秒统计（给 GUI 用）


    // 每秒成功QPS
    public int getAndResetSecondCount() {
        return secondSuccess.getAndSet(0);
    }

    // 每秒失败QPS
    public int getFail() {
        return secondFail.getAndSet(0);
    }

    // 总统计


    public int getTotalSuccess() {
        return totalSuccess.get();
    }

    public int getTotalFail() {
        return totalFail.get();
    }
   //成功百分比
    public double getSuccessRate() {

        int total = totalSuccess.get() + totalFail.get();

        if (total == 0) return 0;

        return totalSuccess.get() * 100.0 / total;
    }

    //  响应时间统计
    public double getAvgRt() {

        if (responseTimes.isEmpty()) return 0;

        long sum = 0;

        for (Long rt : responseTimes) {
            sum += rt;
        }

        return sum * 1.0 / responseTimes.size();
    }
    //获取TP，防止长尾
    public long getTp(double percentile) {

        if (responseTimes.isEmpty()) return 0;

        List<Long> sorted =
                new ArrayList<>(responseTimes);

        Collections.sort(sorted);

        int index =
                (int)(sorted.size() * percentile);

        if (index >= sorted.size()) {
            index = sorted.size() - 1;
        }

        return sorted.get(index);
    }

    //  最终报告
    public String buildFinalReport(long totalTimeMs) {

        int total =
                totalSuccess.get() + totalFail.get();

        double qps = totalTimeMs == 0 ? 0 :
                total / (totalTimeMs / 1000.0);

        return "\n========= 压测报告 =========\n" +
                "总请求: " + total + "\n" +
                "成功: " + totalSuccess.get() + "\n" +
                "失败: " + totalFail.get() + "\n" +
                "成功率: " +
                String.format("%.2f", getSuccessRate()) + "%\n" +
                "QPS: " +
                String.format("%.2f", qps) + "\n" +
                "平均RT: " +
                String.format("%.2f", getAvgRt()) + " ms\n" +
                "TP90: " + getTp(0.9) + " ms\n" +
                "TP99: " + getTp(0.99) + " ms\n";
    }
}