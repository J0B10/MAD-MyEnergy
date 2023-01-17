package io.github.j0b10.mad.myenergy.ui.charging.plan;

import androidx.lifecycle.MutableLiveData;

import java.time.Duration;
import java.time.LocalDateTime;

import io.github.j0b10.mad.myenergy.model.demo.DemoChargingAdapter;
import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeControlAdapter;
import io.github.j0b10.mad.myenergy.model.target.ChargeControls;
import io.github.j0b10.mad.myenergy.ui.viewmodel.SessionAwareViewModel;

public class ChargePlanViewModel extends SessionAwareViewModel {

    final MutableLiveData<LocalDateTime> startTime;
    final MutableLiveData<LocalDateTime> planTime;

    final MutableLiveData<Duration> duration;
    final MutableLiveData<Integer> planCharge;
    final MutableLiveData<Integer> stateCharge;
    final MutableLiveData<Integer> total;

    private ChargeControls chargeControls;

    public ChargePlanViewModel() {
        stateCharge = new MutableLiveData<>(0);
        planCharge = new MutableLiveData<>(0);
        total = new MutableLiveData<>(0);
        startTime = new MutableLiveData<>();
        planTime = new MutableLiveData<>();
        duration = new MutableLiveData<>();
    }

    public ChargeControls controls() {
        return chargeControls;
    }

    @Override
    protected boolean areProvidersLoaded() {
        return chargeControls != null;
    }

    @Override
    protected void loadDemoProviders() {
        chargeControls = DemoChargingAdapter.getInstance();
    }

    @Override
    protected void loadProviders(EVChargerAPI api) {
        chargeControls = new EVChargeControlAdapter(api);
    }

    @Override
    protected void disposeProviders() {
        chargeControls = null;
    }
}