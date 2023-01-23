package io.github.j0b10.mad.myenergy.model.evcharger.adapter;

import static io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeControlAdapter.CHARGE_MODE_EXCESS;
import static io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeControlAdapter.CHARGE_MODE_FAST;
import static io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeControlAdapter.CHARGE_MODE_SMART;
import static io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVChargeControlAdapter.CHARGE_MODE_STOP;
import static io.github.j0b10.mad.myenergy.model.evcharger.adapter.EVStatusAdapter.toMap;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.EXCESS_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.EXCESS_CHARGING_STOPPED;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.FAST_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.FAST_CHARGING_STOPPED;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.SMART_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.UNCONNECTED;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.j0b10.mad.myenergy.model.evcharger.EVChargerAPI;
import io.github.j0b10.mad.myenergy.model.evcharger.dataQueries.SearchQuery;
import io.github.j0b10.mad.myenergy.model.evcharger.dataQueries.SearchQueryItem;
import io.github.j0b10.mad.myenergy.model.evcharger.measurements.Measurement;
import io.github.j0b10.mad.myenergy.model.evcharger.parameters.DeviceParameters;
import io.github.j0b10.mad.myenergy.model.evcharger.parameters.Parameter;
import io.github.j0b10.mad.myenergy.model.evcharger.values.ChannelId;
import io.github.j0b10.mad.myenergy.model.evcharger.values.ComponentId;
import io.github.j0b10.mad.myenergy.model.target.BaseProvider;
import io.github.j0b10.mad.myenergy.model.target.ChargeInfoProvider;
import io.github.j0b10.mad.myenergy.model.target.ChargerState;

public class EVChargeInfoAdapter extends BaseProvider implements ChargeInfoProvider {

    private static final int CHASTT_UNCONNECTED = 200111;

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
    private volatile boolean connected;


    public EVChargeInfoAdapter(EVChargerAPI api) {
        this.api = api;
    }

    @Override
    protected void update() {
        api.getLiveData(List.of(new SearchQueryItem(ComponentId.SELF, null)))
                .enqueue(new PostErrorsCallback<>(error, this::onFetchMeasurements));
        api.getParameters(new SearchQuery(List.of(new SearchQueryItem(ComponentId.SELF, null))))
                .enqueue(new PostErrorsCallback<>(error, this::onFetchParameters));
    }

    private void onFetchMeasurements(List<Measurement> measurementList) {
        Map<String, Measurement> measurements = toMap(measurementList, m -> m.channelId);
        Measurement chargeWH = measurements.get(ChannelId.Measurement.EV_CHARGE_WH);
        Measurement evConsW = measurements.get(ChannelId.Measurement.EV_W);
        Measurement chState = measurements.get(ChannelId.Measurement.EV_CHARGE_STATE);
        boolean connected = Optional.ofNullable(chState)
                .map(m -> m.values.get(0).value)
                .map(val -> (int) ((double) val) != CHASTT_UNCONNECTED).orElse(false);
        double charge = Optional.ofNullable(chargeWH)
                .map(m -> m.values.get(0).value / 1000).orElse(0.0);
        double evConsumption = Optional.ofNullable(evConsW)
                .map(m -> m.values.get(0).value / 1000).orElse(0.0);
        setConnected(connected);
        if (!connected) this.chargerState.postValue(UNCONNECTED);
        this.charge.postValue(charge);
        this.evConsumption.postValue(evConsumption);
    }

    private void onFetchParameters(List<DeviceParameters> deviceParameterList) {
        Map<String, Parameter> parameters = toMap(deviceParameterList.get(0).values, p -> p.channelId);
        Parameter goalW = parameters.get(ChannelId.Parameter.CHARGE_ENERGY);
        Parameter state = parameters.get(ChannelId.Parameter.CHARGE_MODE);
        Parameter planEnd = parameters.get(ChannelId.Parameter.EV_CHARGE_END_TM);
        double goal = Optional.ofNullable(goalW)
                .map(p -> p.value).map(Double::parseDouble).orElse(0.0);
        LocalDateTime time = Optional.ofNullable(planEnd)
                .map(p -> p.value).map(Long::parseLong).map(Instant::ofEpochSecond)
                .map(t -> LocalDateTime.ofInstant(t, ZoneId.systemDefault()))
                .orElse(null);
        ChargerState chState = Optional.ofNullable(state).map(s ->
                switch (s.value) {
                    case CHARGE_MODE_EXCESS -> EXCESS_CHARGING;
                    case CHARGE_MODE_SMART -> SMART_CHARGING;
                    case CHARGE_MODE_STOP -> {
                        if (state.possibleValues == null)
                            throw new IllegalArgumentException("possible values of "
                                    + ChannelId.Parameter.CHARGE_MODE + " not provided");
                        if (Arrays.asList(state.possibleValues).contains(CHARGE_MODE_FAST)) {
                            yield FAST_CHARGING_STOPPED;
                        } else {
                            //fixme always EXCESS_CHARGING_STOPPED,
                            // no matter what mode the charger was in before stop
                            yield EXCESS_CHARGING_STOPPED;
                        }
                    }
                    case CHARGE_MODE_FAST -> FAST_CHARGING;
                    default ->
                            throw new IllegalArgumentException("unknown charge state " + s.value);
                }).orElse(UNCONNECTED);
        Log.i("EVChargeInfo", "state=" + chState);
        this.goal.postValue(goal);
        if (time != null) this.planEndTime.postValue(time);
        if (isConnected()) this.chargerState.postValue(chState);
    }

    private synchronized boolean isConnected() {
        return this.connected;
    }

    private synchronized void setConnected(boolean connected) {
        this.connected = connected;
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
