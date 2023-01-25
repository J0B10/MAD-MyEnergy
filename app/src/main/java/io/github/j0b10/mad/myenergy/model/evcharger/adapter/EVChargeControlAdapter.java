package io.github.j0b10.mad.myenergy.model.evcharger.adapter;

import static java.lang.String.valueOf;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.evcharger.parameters.Parameter;
import io.github.j0b10.mad.myenergy.model.evcharger.parameters.ParameterPutQuery;
import io.github.j0b10.mad.myenergy.model.evcharger.values.ChannelId;
import io.github.j0b10.mad.myenergy.model.target.ChargeControls;
import io.github.j0b10.mad.myenergy.model.target.ChargerState;

public class EVChargeControlAdapter implements ChargeControls {

    public static final String
            CHARGE_MODE_FAST = "4718",
            CHARGE_MODE_EXCESS = "4719",
            CHARGE_MODE_SMART = "4720",
            CHARGE_MODE_STOP = "4721";
    private final EVChargerAPI api;

    private final MutableLiveData<Exception> error = new MutableLiveData<>();

    public EVChargeControlAdapter(EVChargerAPI api) {
        this.api = api;
    }

    @Override
    public void planCharging(Duration duration, int total, Runnable onCharging) {
        Instant time = Instant.now();
        ParameterPutQuery query = new ParameterPutQuery(List.of(
                new Parameter(ChannelId.Parameter.CHARGE_MODE, CHARGE_MODE_SMART, time),
                new Parameter(ChannelId.Parameter.CHARGE_DURATION, valueOf(duration.toMinutes()), time),
                new Parameter(ChannelId.Parameter.CHARGE_ENERGY, valueOf(total), time)
        ));
        api.setParameters(query).enqueue(new PostErrorsCallback<>(error, onCharging));
    }

    @Override
    public void startCharging(ChargerState mode, Runnable onCharging) {
        Instant time = Instant.now();
        String chargeMode = switch (mode) {
            case EXCESS_CHARGING -> CHARGE_MODE_EXCESS;
            case SMART_CHARGING -> CHARGE_MODE_SMART;
            case FAST_CHARGING -> CHARGE_MODE_FAST;
            default -> throw new IllegalArgumentException("can't start charging in mode " + mode);
        };
        ParameterPutQuery query = new ParameterPutQuery(List.of(
                new Parameter(ChannelId.Parameter.CHARGE_MODE, chargeMode, time)
        ));
        api.setParameters(query).enqueue(new PostErrorsCallback<>(error, onCharging));
    }

    @Override
    public void stopCharging(Runnable onStopped) {
        Instant time = Instant.now();
        ParameterPutQuery query = new ParameterPutQuery(List.of(
                new Parameter(ChannelId.Parameter.CHARGE_MODE, CHARGE_MODE_STOP, time)
        ));
        api.setParameters(query).enqueue(new PostErrorsCallback<>(error, onStopped));

    }

    @Override
    public void setChargeALim(int limit, Runnable onLimitSet) {
        Instant time = Instant.now();
        ParameterPutQuery query = new ParameterPutQuery(List.of(
                new Parameter(ChannelId.Parameter.EV_CHARGE_A_LIM, valueOf(limit), time)
        ));
        api.setParameters(query).enqueue(new PostErrorsCallback<>(error, onLimitSet));
    }

    @Override
    public LiveData<Exception> error() {
        return error;
    }
}
