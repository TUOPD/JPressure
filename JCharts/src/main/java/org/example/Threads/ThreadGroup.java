package org.example.Threads;

import org.example.Assertion.Assertion;
import org.example.Listen.Listener;
import org.example.Sample.Sampler;
import org.example.SampleResult.SampleResult;
import org.example.Timer.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadGroup {

    private final int threads;
    private final int durationSeconds;
    private final int rampUpSeconds;
    private final Sampler sampler;

    private final List<Timer> timers = new CopyOnWriteArrayList<>();
    private final List<Assertion> assertions = new CopyOnWriteArrayList<>();
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    private volatile boolean running = true;

    public ThreadGroup(int threads,
                       int durationSeconds,
                       int rampUpSeconds,
                       Sampler sampler) {

        this.threads = threads;
        this.durationSeconds = durationSeconds;
        this.rampUpSeconds = rampUpSeconds;
        this.sampler = sampler;
    }
    public void addTimer(Timer timer) {
        timers.add(timer);
    }

    public void addAssertion(Assertion assertion) {
        assertions.add(assertion);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void stop() {
        running = false;
    }

    public void start() throws InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(threads);

        long startTime = System.nanoTime();
        long endTime = startTime + TimeUnit.SECONDS.toNanos(durationSeconds);

        long rampUpInterval = rampUpSeconds > 0
                ? (rampUpSeconds * 1000L / threads)
                : 0;

        for (int i = 0; i < threads; i++) {

            pool.submit(() -> {

                while (running && System.nanoTime() < endTime) {


                    for (Timer timer : timers) {
                        timer.delay();
                    }


                    SampleResult result = sampler.sample();


                    for (Assertion assertion : sampler.defaultAssertions()) {
                        this.assertions.add(assertion);
                    }


                    for (Listener listener : listeners) {
                        listener.onSampleResult(result);
                    }
                }
            });


            if (rampUpInterval > 0) {
                Thread.sleep(rampUpInterval);
            }
        }

        pool.shutdown();
        pool.awaitTermination(durationSeconds + rampUpSeconds + 5,
                TimeUnit.SECONDS);

        running = false;
    }
}