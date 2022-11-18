package io.github.j0b10.mad.myenergy.ui.status;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatusViewModel extends ViewModel {

    final MutableLiveData<Double> gridFeedIn;
    final MutableLiveData<Double> homeConsumption;
    final MutableLiveData<Double> evConsumption;
    final MutableLiveData<Double> pvProduction;

    public StatusViewModel() {
        this.gridFeedIn = new MutableLiveData<>();
        this.homeConsumption = new MutableLiveData<>();
        this.evConsumption = new MutableLiveData<>();
        this.pvProduction = new MutableLiveData<>();
        gridFeedIn.setValue(0.0);
        homeConsumption.setValue(0.0);
        evConsumption.setValue(0.0);
        pvProduction.setValue(0.0);
    }
}