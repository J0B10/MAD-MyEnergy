package io.github.j0b10.mad.myenergy.ui.charging.plan;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;

public class ChargePlanViewModel extends ViewModel {

    final MutableLiveData<Float> carWLTP;
    final MutableLiveData<Integer> carCapacity;
    final MutableLiveData<LocalDateTime> planTime;
    final MutableLiveData<Integer> planCharge;
    final MutableLiveData<Integer> stateCharge;
    final MutableLiveData<Integer> total;

    public ChargePlanViewModel() {
        carWLTP = new MutableLiveData<>(15.0f);
        stateCharge = new MutableLiveData<>(0);
        planCharge = new MutableLiveData<>(0);
        total = new MutableLiveData<>(0);
        planTime = new MutableLiveData<>();
        carCapacity = new MutableLiveData<>(40);
    }
}