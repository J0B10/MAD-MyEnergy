package io.github.j0b10.mad.myenergy.model.target;

import androidx.lifecycle.LiveData;

import java.time.LocalDateTime;

public interface ChargeInfoProvider extends Provider {

    LiveData<ChargerState> chargerState();

    LiveData<Double> evConsumption();

    LiveData<Double> charge();

    LiveData<Double> goal();

    LiveData<LocalDateTime> planEndTime();

}
