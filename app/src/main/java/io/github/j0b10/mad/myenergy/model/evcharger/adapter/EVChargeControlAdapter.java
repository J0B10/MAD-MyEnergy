package io.github.j0b10.mad.myenergy.model.evcharger.adapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Duration;

import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.target.ChargeControls;
import io.github.j0b10.mad.myenergy.model.target.ChargerState;

public class EVChargeControlAdapter implements ChargeControls {

    private final EVChargerAPI api;

    private final MutableLiveData<Exception> error = new MutableLiveData<>();

    public EVChargeControlAdapter(EVChargerAPI api) {
        this.api = api;
    }

    @Override
    public void planCharging(Duration duration, double total, Runnable onCharging) {

    }

    @Override
    public void startCharging(ChargerState mode, Runnable onCharging) {

    }

    @Override
    public void stopCharging(Runnable onStopped) {

    }

    @Override
    public LiveData<Exception> error() {
        return error;
    }
}
