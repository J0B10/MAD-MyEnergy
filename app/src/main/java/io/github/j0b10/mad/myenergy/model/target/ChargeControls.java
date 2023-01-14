package io.github.j0b10.mad.myenergy.model.target;

import androidx.lifecycle.LiveData;

import java.time.Duration;

public interface ChargeControls {

    void planCharging(Duration duration, double total, Runnable onCharging);

    void startCharging(ChargerState mode, Runnable onCharging);

    void stopCharging(Runnable onStopped);

    LiveData<Exception> error();
}
