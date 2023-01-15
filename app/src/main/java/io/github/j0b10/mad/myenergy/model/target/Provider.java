package io.github.j0b10.mad.myenergy.model.target;

import androidx.lifecycle.LiveData;

import java.time.Duration;

public interface Provider {
    void start();

    void stop();

    void configureInterval(Duration fetchInterval);

    void requestUpdateNow();

    LiveData<Exception> error();
}
