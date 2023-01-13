package io.github.j0b10.mad.myenergy.model.target;

import java.time.Duration;

public interface Provider {
    void start();

    void stop();

    void configureInterval(Duration fetchInterval);
}
