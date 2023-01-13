package io.github.j0b10.mad.myenergy.model.target;

import androidx.lifecycle.LiveData;

public interface StatusProvider extends Provider {
    LiveData<Double> gridFeedIn();

    LiveData<Double> homeConsumption();

    LiveData<Double> evConsumption();

    LiveData<Double> pvProduction();
}
