package io.github.j0b10.mad.myenergy.model.evcharger.adapter;

import static io.github.j0b10.mad.myenergy.model.target.ChargerState.*;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDateTime;

import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.target.BaseProvider;
import io.github.j0b10.mad.myenergy.model.target.ChargeInfoProvider;
import io.github.j0b10.mad.myenergy.model.target.ChargerState;

public class EVChargeInfoAdapter extends BaseProvider implements ChargeInfoProvider {

    private final EVChargerAPI api;

    private final MutableLiveData<Boolean>
            isCharging = new MutableLiveData<>(false);
    private final MutableLiveData<ChargerState>
            chargerState = new MutableLiveData<>(UNCONNECTED);
    private final MutableLiveData<Double>
            evConsumption = new MutableLiveData<>(0.0),
            charge = new MutableLiveData<>(0.0),
            goal = new MutableLiveData<>(null);
    private final MutableLiveData<LocalDateTime> planEndTime = new MutableLiveData<>();


    public EVChargeInfoAdapter(EVChargerAPI api) {
        this.api = api;
    }


    @Override
    protected void update() throws Exception {

    }

    @Override
    public LiveData<ChargerState> chargerState() {
        return chargerState;
    }

    @Override
    public LiveData<Double> evConsumption() {
        return evConsumption;
    }

    @Override
    public LiveData<Double> charge() {
        return charge;
    }

    @Override
    public LiveData<Double> goal() {
        return goal;
    }

    @Override
    public LiveData<LocalDateTime> planEndTime() {
        return planEndTime;
    }
}
