package org.example.Service;


import org.example.Data.MetricsResponse;
import org.example.Data.TestRequest;
import org.example.Factory.SamplerFactory;
import org.example.Sample.Sampler;
import org.example.Threads.ThreadGroup;
import org.example.Assertion.StatusAssertion;
import org.example.Listen.StatsListener;
import org.example.Timer.QpsTimer;

import org.example.Samples.HttpSampler;
import org.springframework.stereotype.Service;

@Service
public class PressureService {

    private ThreadGroup threadGroup;
    private StatsListener statsListener;

    public void start(TestRequest request) {

        Sampler sampler = SamplerFactory.createSampler(request);

        threadGroup = new ThreadGroup(
                request.getThreads(),
                request.getDuration(),
                0,
                sampler
        );

        statsListener = new StatsListener();

        threadGroup.addTimer(new QpsTimer(request.getQps()));
        threadGroup.addAssertion(new StatusAssertion(200));
        threadGroup.addListener(statsListener);

        new Thread(() -> {
            try {
                threadGroup.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stop() {
        if (threadGroup != null) {
            threadGroup.stop();
        }
    }

    public MetricsResponse getMetrics() {

        if (statsListener == null) {
            return new MetricsResponse();
        }

        MetricsResponse response = new MetricsResponse();
        response.setQps(statsListener.getAndResetSecondCount());
        response.setTotalSuccess(statsListener.getTotalSuccess());
        response.setTotalFail(statsListener.getTotalFail());
        response.setAvgRt(statsListener.getAvgRt());
        response.setTp90(statsListener.getTp(0.9));
        response.setTp99(statsListener.getTp(0.99));

        return response;
    }
}