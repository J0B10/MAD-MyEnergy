package io.github.j0b10.mad.myenergy.model.demo;

import static io.github.j0b10.mad.myenergy.model.target.ChargerState.EXCESS_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.EXCESS_CHARGING_STOPPED;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.FAST_CHARGING_STOPPED;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.SMART_CHARGING;
import static io.github.j0b10.mad.myenergy.model.target.ChargerState.SMART_CHARGING_STOPPED;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

import io.github.j0b10.mad.myenergy.model.target.BaseProvider;
import io.github.j0b10.mad.myenergy.model.target.ChargeControls;
import io.github.j0b10.mad.myenergy.model.target.ChargeInfoProvider;
import io.github.j0b10.mad.myenergy.model.target.ChargerState;

public class DemoChargingAdapter extends BaseProvider implements ChargeInfoProvider, ChargeControls {

    private static DemoChargingAdapter instance;

    public static DemoChargingAdapter getInstance() {
        if (instance == null) instance = new DemoChargingAdapter();
        return instance;
    }

    private DemoChargingAdapter() {
    }

    private static final long MS_PER_H = Duration.ofHours(1).toMillis();

    private final Random random = new Random();

    private volatile ChargerState _state = EXCESS_CHARGING_STOPPED;
    private volatile double _charge = 0, _goal = 0, _rate = 0;
    private final MutableLiveData<ChargerState>
            chargerState = new MutableLiveData<>(EXCESS_CHARGING_STOPPED);
    private final MutableLiveData<Double>
            evConsumption = new MutableLiveData<>(0.0),
            charge = new MutableLiveData<>(0.0),
            goal = new MutableLiveData<>(null);
    private final MutableLiveData<LocalDateTime> planEndTime = new MutableLiveData<>();

    private final MutableLiveData<Integer> chargeALim = new MutableLiveData<>(16);


    @Override
    protected void update() {
        ChargerState oldState, state;
        double charge, rate;
        synchronized (this) {
            if (!_state.isCharging()) return;
            if (!_state.isQuickCharge()) {
                _rate = 1.3 + random.nextDouble() * (11.0 - 1.3);
            }
            _charge += (_rate * fetchInterval.toMillis()) / MS_PER_H;
            oldState = _state;
            if (_state == SMART_CHARGING && _charge >= _goal) {
                _state = EXCESS_CHARGING;
                _charge = _goal;
            }
            state = _state;
            charge = _charge;
            rate = _rate;
        }
        this.charge.postValue(charge);
        evConsumption.postValue(rate);
        if (state != oldState) chargerState.postValue(state);
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

    @Override
    public LiveData<Integer> chargeAlim() {
        return chargeALim;
    }

    @Override
    public void planCharging(Duration duration, int total, Runnable onCharging) {
        synchronized (this) {
            _state = SMART_CHARGING;
            _goal = total;
            _charge = 0.0;
        }
        planEndTime.setValue(LocalDateTime.now().plus(duration));
        goal.setValue((double) total);
        charge.setValue(0.0);
        chargerState.setValue(SMART_CHARGING);
        onCharging.run();
    }

    @Override
    public void startCharging(ChargerState mode, Runnable onCharging) {
        if (!mode.isCharging()) {
            throw new IllegalArgumentException("can't start charging in mode " + mode);
        }
        double rate = (mode == EXCESS_CHARGING) ? 0.0 : 11.0;
        synchronized (this) {
            _rate = rate;
            _goal = -1;
            _charge = 0.0;
            _state = mode;
        }
        goal.setValue(null);
        chargerState.setValue(mode);
        charge.setValue(0.0);
        evConsumption.setValue(rate);
        onCharging.run();
    }

    @Override
    public void stopCharging(Runnable onStopped) {
        ChargerState state;
        synchronized (this) {
            state = switch (_state) {
                case SMART_CHARGING -> SMART_CHARGING_STOPPED;
                case FAST_CHARGING -> FAST_CHARGING_STOPPED;
                case EXCESS_CHARGING -> EXCESS_CHARGING_STOPPED;
                default -> _state;
            };
            _goal = -1;
            _rate = 0.0;
            _state = state;
        }
        chargerState.setValue(state);
        evConsumption.setValue(0.0);
        onStopped.run();
    }

    @Override
    public void setChargeALim(int limit, Runnable onSetLimit) {
        synchronized (this) {
            if (!_state.isQuickCharge())
                throw new IllegalStateException("can't set limit in state " + _state);
        }
        chargeALim.setValue(limit);
        onSetLimit.run();
    }
}
