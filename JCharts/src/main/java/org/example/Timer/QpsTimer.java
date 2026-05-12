package org.example.Timer;


public class QpsTimer implements Timer {

    private final long intervalNs;
    private long nextTime = System.nanoTime();

    public QpsTimer(int qps) {
        this.intervalNs = 1_000_000_000L / qps;
    }

    @Override
    public synchronized void delay() {

        long now = System.nanoTime();

        if (now < nextTime) {
            long sleepTime = nextTime - now;
            try {
                Thread.sleep(
                        sleepTime / 1_000_000,
                        (int) (sleepTime % 1_000_000)
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        nextTime = Math.max(nextTime + intervalNs, System.nanoTime());
    }
}