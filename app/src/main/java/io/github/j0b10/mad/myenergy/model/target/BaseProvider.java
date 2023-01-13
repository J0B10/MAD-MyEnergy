package io.github.j0b10.mad.myenergy.model.target;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class BaseProvider implements Provider {


    protected ScheduledExecutorService executor;
    protected Duration fetchInterval;
    protected final MutableLiveData<Exception> error = new MutableLiveData<>();


    @Override
    public void configureInterval(Duration fetchInterval) {
        if (executor != null)
            throw new IllegalStateException("cant configure interval while executor is running");
        this.fetchInterval = fetchInterval;
    }

    protected abstract void update() throws Exception;

    @Override
    public void start() {
        if (fetchInterval == null)
            throw new IllegalStateException("configure fetch interval before starting the provider");
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                update();
                error.postValue(null);
            } catch (Exception e) {
                error.postValue(e);
            }
        }, 0, fetchInterval.toMillis(), MILLISECONDS);
    }

    @Override
    public void stop() {
        executor.shutdownNow();
        executor = null;
    }

    @Override
    public LiveData<Exception> error() {
        return error;
    }
}
