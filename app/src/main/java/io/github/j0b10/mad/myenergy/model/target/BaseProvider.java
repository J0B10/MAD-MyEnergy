package io.github.j0b10.mad.myenergy.model.target;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class BaseProvider implements Provider {


    protected ScheduledExecutorService executor;
    protected Duration fetchInterval;


    @Override
    public void configureInterval(Duration fetchInterval) {
        if (executor != null)
            throw new IllegalStateException("cant configure interval while executor is running");
        this.fetchInterval = fetchInterval;
    }

    protected abstract void update();

    @Override
    public void start() {
        if (fetchInterval == null)
            throw new IllegalStateException("configure fetch interval before starting the provider");
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::update, 0, fetchInterval.toMillis(), MILLISECONDS);
    }

    @Override
    public void stop() {
        executor.shutdownNow();
        executor = null;
    }
}
